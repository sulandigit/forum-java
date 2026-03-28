package pub.developers.forum.infrastructure.cache;

import com.alibaba.fastjson.JSON;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import pub.developers.forum.common.enums.CacheBizTypeEn;
import pub.developers.forum.common.enums.ErrorCodeEn;
import pub.developers.forum.common.exception.BizException;
import pub.developers.forum.common.support.SafesUtil;
import pub.developers.forum.domain.service.CacheService;
import pub.developers.forum.infrastructure.dal.dao.CacheDAO;
import pub.developers.forum.infrastructure.dal.dataobject.CacheDO;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 基于数据库的缓存服务实现
 * 使用内存缓存 + 定时持久化策略
 * 
 * @author Qiangqiang.Bian
 * @create 20/10/22
 **/
@Slf4j
@Service
public class DbCacheServiceImpl implements CacheService {

    private static final String KEY_SEPARATOR = ":";
    private static final long NO_EXPIRE = -1L;
    
    @Resource
    private CacheDAO cacheDAO;

    /**
     * 全部缓存 - 使用ConcurrentHashMap保证线程安全
     */
    private static final Map<String, StringValue> ALL_CACHE = new ConcurrentHashMap<>();

    /**
     * 更新缓存键集合 - 使用线程安全集合
     */
    private static final Set<String> MODIFY_KEYS = ConcurrentHashMap.newKeySet();

    /**
     * 新增缓存键集合 - 使用线程安全集合
     */
    private static final Set<String> NEW_KEYS = ConcurrentHashMap.newKeySet();

    /**
     * 删除缓存键集合 - 使用线程安全集合
     */
    private static final Set<String> DELETE_KEYS = ConcurrentHashMap.newKeySet();

    @Override
    public boolean set(CacheBizTypeEn bizType, String key, String value) {
        checkKey(key);

        String buildKey = buildKey(bizType, key);
        boolean isExist = ALL_CACHE.containsKey(buildKey);
        
        ALL_CACHE.put(buildKey, new StringValue(bizType.getValue(), value));
        
        // 标记为修改或新增
        if (isExist) {
            MODIFY_KEYS.add(buildKey);
            // 如果之前在新增集合中，移除（避免重复操作）
            NEW_KEYS.remove(buildKey);
        } else {
            NEW_KEYS.add(buildKey);
        }

        return true;
    }

    @Override
    public boolean setAndExpire(CacheBizTypeEn bizType, String key, String value, Long seconds) {
        checkKey(key);
        
        if (seconds == null || seconds <= 0) {
            log.warn("过期时间设置无效，key: {}, seconds: {}", key, seconds);
            return set(bizType, key, value);
        }

        String buildKey = buildKey(bizType, key);
        boolean isExist = ALL_CACHE.containsKey(buildKey);
        
        ALL_CACHE.put(buildKey, new StringValue(bizType.getValue(), value, seconds));
        
        // 标记为修改或新增
        if (isExist) {
            MODIFY_KEYS.add(buildKey);
            NEW_KEYS.remove(buildKey);
        } else {
            NEW_KEYS.add(buildKey);
        }

        return true;
    }

    @Override
    public String get(CacheBizTypeEn bizType, String key) {
        checkKey(key);

        String buildKey = buildKey(bizType, key);
        StringValue stringValue = ALL_CACHE.get(buildKey);
        
        // 检查是否过期
        if (stringValue != null && stringValue.isExpired()) {
            // 过期则删除
            ALL_CACHE.remove(buildKey);
            DELETE_KEYS.add(buildKey);
            return null;
        }
        
        return stringValue == null ? null : stringValue.getValue();
    }

    @Override
    public Boolean exists(CacheBizTypeEn bizType, String key) {
        checkKey(key);

        String buildKey = buildKey(bizType, key);
        StringValue stringValue = ALL_CACHE.get(buildKey);
        
        // 检查是否存在且未过期
        if (stringValue != null && stringValue.isExpired()) {
            ALL_CACHE.remove(buildKey);
            DELETE_KEYS.add(buildKey);
            return false;
        }
        
        return stringValue != null;
    }

    @Override
    public Boolean del(CacheBizTypeEn bizType, String key) {
        checkKey(key);

        String buildKey = buildKey(bizType, key);
        
        if (ALL_CACHE.remove(buildKey) != null) {
            DELETE_KEYS.add(buildKey);
            // 从新增和修改集合中移除（避免无效操作）
            NEW_KEYS.remove(buildKey);
            MODIFY_KEYS.remove(buildKey);
            return true;
        }
        
        return false;
    }

    // -------------------------------- 缓存生命周期管理

    /**
     * 应用启动时加载缓存数据到内存
     */
    @PostConstruct
    public void postConstruct() {
        log.info("开始加载缓存数据到内存...");
        try {
            List<CacheDO> cacheDOList = SafesUtil.ofList(cacheDAO.getAll());
            cacheDOList.forEach(cacheDO -> {
                try {
                    StringValue stringValue = JSON.parseObject(cacheDO.getValue(), StringValue.class);
                    // 过滤已过期的数据
                    if (!stringValue.isExpired()) {
                        ALL_CACHE.put(cacheDO.getKey(), stringValue);
                    }
                } catch (Exception e) {
                    log.error("解析缓存数据失败, key: {}, value: {}", cacheDO.getKey(), cacheDO.getValue(), e);
                }
            });
            log.info("缓存数据加载完成, 共加载 {} 条记录", ALL_CACHE.size());
        } catch (Exception e) {
            log.error("加载缓存数据失败", e);
        }
    }

    /**
     * 应用关闭时持久化缓存数据
     */
    @PreDestroy
    public void preDestroy() {
        log.info("应用关闭，开始持久化缓存数据...");
        try {
            persistence();
            log.info("缓存数据持久化完成");
        } catch (Exception e) {
            log.error("缓存数据持久化失败", e);
        }
    }

    /**
     * 定时清理过期缓存并持久化
     * 每60秒执行一次
     */
    @Scheduled(cron = "0/60 * * * * ?")
    public void task() {
        try {
            // 清理过期缓存
            cleanExpiredCache();
            // 持久化到数据库
            persistence();
        } catch (Exception e) {
            log.error("定时任务执行失败", e);
        }
    }
    
    /**
     * 清理过期的缓存数据
     */
    private void cleanExpiredCache() {
        long now = System.currentTimeMillis();
        List<String> expiredKeys = SafesUtil.ofMap(ALL_CACHE)
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue().isExpired(now))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        
        if (!expiredKeys.isEmpty()) {
            expiredKeys.forEach(key -> {
                ALL_CACHE.remove(key);
                DELETE_KEYS.add(key);
                // 从新增和修改集合中移除
                NEW_KEYS.remove(key);
                MODIFY_KEYS.remove(key);
            });
            log.debug("清理过期缓存 {} 条", expiredKeys.size());
        }
    }

    /**
     * 持久化缓存到数据库
     */
    private synchronized void persistence() {
        try {
            // 1. 处理删除操作
            persistenceDelete();
            
            // 2. 处理更新操作
            persistenceUpdate();
            
            // 3. 处理新增操作
            persistenceInsert();
        } catch (Exception e) {
            log.error("持久化缓存失败", e);
        }
    }
    
    /**
     * 持久化删除操作
     */
    private void persistenceDelete() {
        if (CollectionUtils.isEmpty(DELETE_KEYS)) {
            return;
        }
        
        try {
            Set<String> keysToDelete = new HashSet<>(DELETE_KEYS);
            cacheDAO.batchDeleteByKeys(keysToDelete);
            DELETE_KEYS.clear();
            log.debug("批量删除缓存 {} 条", keysToDelete.size());
        } catch (Exception e) {
            log.error("批量删除缓存失败", e);
        }
    }
    
    /**
     * 持久化更新操作
     */
    private void persistenceUpdate() {
        if (CollectionUtils.isEmpty(MODIFY_KEYS)) {
            return;
        }
        
        Set<String> keysToUpdate = new HashSet<>(MODIFY_KEYS);
        int successCount = 0;
        
        for (String key : keysToUpdate) {
            StringValue stringValue = ALL_CACHE.get(key);
            if (stringValue != null) {
                try {
                    cacheDAO.updateByKey(key, JSON.toJSONString(stringValue));
                    successCount++;
                } catch (Exception e) {
                    log.error("更新缓存失败, key: {}", key, e);
                }
            }
        }
        
        MODIFY_KEYS.clear();
        if (successCount > 0) {
            log.debug("批量更新缓存 {} 条", successCount);
        }
    }
    
    /**
     * 持久化新增操作
     */
    private void persistenceInsert() {
        if (CollectionUtils.isEmpty(NEW_KEYS)) {
            return;
        }
        
        Set<String> keysToInsert = new HashSet<>(NEW_KEYS);
        List<CacheDO> newCacheDOS = new ArrayList<>();
        
        for (String key : keysToInsert) {
            StringValue stringValue = ALL_CACHE.get(key);
            if (stringValue != null) {
                CacheDO cacheDO = CacheDO.builder()
                        .key(key)
                        .value(JSON.toJSONString(stringValue))
                        .type(stringValue.getType())
                        .build();
                cacheDO.initBase();
                newCacheDOS.add(cacheDO);
            }
        }
        
        NEW_KEYS.clear();
        
        if (!newCacheDOS.isEmpty()) {
            try {
                // 优先使用批量插入
                cacheDAO.insertBatch(newCacheDOS);
                log.debug("批量新增缓存 {} 条", newCacheDOS.size());
            } catch (Exception e) {
                log.warn("批量插入失败，尝试单条插入", e);
                // 批量插入失败时，降级为单条插入
                int successCount = 0;
                for (CacheDO cacheDO : newCacheDOS) {
                    try {
                        cacheDAO.insert(cacheDO);
                        successCount++;
                    } catch (Exception ex) {
                        log.error("插入缓存失败, key: {}", cacheDO.getKey(), ex);
                    }
                }
                log.debug("单条插入缓存成功 {} 条", successCount);
            }
        }
    }

    /**
     * 校验 key 是否合法
     */
    private void checkKey(String key) {
        if (StringUtils.isEmpty(key)) {
            throw new BizException(ErrorCodeEn.COMMON_CACHE_KEY_EMPTY);
        }
    }

    /**
     * 构建缓存键
     */
    private String buildKey(CacheBizTypeEn bizType, String key) {
        return buildKey(bizType.getValue(), key);
    }

    /**
     * 构建缓存键
     */
    private String buildKey(String bizType, String key) {
        return bizType + KEY_SEPARATOR + key;
    }

    /**
     * 缓存值对象
     */
    @Data
    @NoArgsConstructor
    private static class StringValue {
        /**
         * 缓存值
         */
        private String value;
        
        /**
         * 过期时间戳（毫秒），-1 表示永不过期
         */
        private Long expire;
        
        /**
         * 缓存类型
         */
        private String type;

        /**
         * 构造带过期时间的缓存值
         */
        private StringValue(String type, String value, Long seconds) {
            this.type = type;
            this.value = value;
            this.expire = System.currentTimeMillis() + seconds * 1000;
        }

        /**
         * 构造永不过期的缓存值
         */
        private StringValue(String type, String value) {
            this.type = type;
            this.value = value;
            this.expire = NO_EXPIRE;
        }
        
        /**
         * 判断是否已过期
         */
        private boolean isExpired() {
            return isExpired(System.currentTimeMillis());
        }
        
        /**
         * 判断在指定时间点是否已过期
         */
        private boolean isExpired(long currentTimeMillis) {
            return expire != NO_EXPIRE && currentTimeMillis >= expire;
        }
    }
}