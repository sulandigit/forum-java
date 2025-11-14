package pub.developers.forum.infrastructure.cache;

import com.github.benmanes.caffeine.cache.Cache;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import pub.developers.forum.common.enums.CacheBizTypeEn;
import pub.developers.forum.common.enums.ErrorCodeEn;
import pub.developers.forum.common.exception.BizException;
import pub.developers.forum.domain.service.CacheService;

import javax.annotation.Resource;

/**
 * 基于Caffeine的本地缓存实现
 * 
 * @author forum-java
 * @create 2025/11/14
 * @desc Caffeine本地缓存服务实现类
 */
@Slf4j
@Service
@ConditionalOnProperty(name = "cache.type", havingValue = "caffeine")
public class CaffeineCacheServiceImpl implements CacheService {

    @Resource
    private Cache<String, CacheValue> caffeineExpireCache;

    /**
     * 存储缓存
     * 
     * @param bizType 业务类型
     * @param key 缓存键
     * @param value 缓存值
     * @return 是否成功
     */
    @Override
    public boolean set(CacheBizTypeEn bizType, String key, String value) {
        checkKey(key);
        String cacheKey = buildKey(bizType, key);
        
        CacheValue cacheValue = new CacheValue();
        cacheValue.setValue(value);
        cacheValue.setBizType(bizType.getValue());
        // 不设置过期时间,永久有效
        cacheValue.setExpireTime(null);
        
        caffeineExpireCache.put(cacheKey, cacheValue);
        log.debug("Caffeine缓存存储成功: key={}, bizType={}", cacheKey, bizType.getValue());
        
        return true;
    }

    /**
     * 存储缓存并设置过期时间
     * 
     * @param bizType 业务类型
     * @param key 缓存键
     * @param value 缓存值
     * @param seconds 过期时间(秒)
     * @return 是否成功
     */
    @Override
    public boolean setAndExpire(CacheBizTypeEn bizType, String key, String value, Long seconds) {
        checkKey(key);
        String cacheKey = buildKey(bizType, key);
        
        CacheValue cacheValue = new CacheValue();
        cacheValue.setValue(value);
        cacheValue.setBizType(bizType.getValue());
        // 设置过期时间戳(毫秒)
        cacheValue.setExpireTime(System.currentTimeMillis() + seconds * 1000);
        
        caffeineExpireCache.put(cacheKey, cacheValue);
        log.debug("Caffeine缓存存储成功(带过期时间): key={}, bizType={}, expireSeconds={}", 
                  cacheKey, bizType.getValue(), seconds);
        
        return true;
    }

    /**
     * 获取缓存值
     * 
     * @param bizType 业务类型
     * @param key 缓存键
     * @return 缓存值
     */
    @Override
    public String get(CacheBizTypeEn bizType, String key) {
        checkKey(key);
        String cacheKey = buildKey(bizType, key);
        
        CacheValue cacheValue = caffeineExpireCache.getIfPresent(cacheKey);
        
        if (cacheValue == null) {
            log.debug("Caffeine缓存未命中: key={}, bizType={}", cacheKey, bizType.getValue());
            return null;
        }
        
        // 检查是否过期
        if (cacheValue.getExpireTime() != null && 
            System.currentTimeMillis() > cacheValue.getExpireTime()) {
            // 已过期,删除缓存
            caffeineExpireCache.invalidate(cacheKey);
            log.debug("Caffeine缓存已过期并删除: key={}, bizType={}", cacheKey, bizType.getValue());
            return null;
        }
        
        log.debug("Caffeine缓存命中: key={}, bizType={}", cacheKey, bizType.getValue());
        return cacheValue.getValue();
    }

    /**
     * 判断缓存是否存在
     * 
     * @param bizType 业务类型
     * @param key 缓存键
     * @return 是否存在
     */
    @Override
    public Boolean exists(CacheBizTypeEn bizType, String key) {
        checkKey(key);
        String cacheKey = buildKey(bizType, key);
        
        CacheValue cacheValue = caffeineExpireCache.getIfPresent(cacheKey);
        
        if (cacheValue == null) {
            return false;
        }
        
        // 检查是否过期
        if (cacheValue.getExpireTime() != null && 
            System.currentTimeMillis() > cacheValue.getExpireTime()) {
            caffeineExpireCache.invalidate(cacheKey);
            return false;
        }
        
        return true;
    }

    /**
     * 删除缓存
     * 
     * @param bizType 业务类型
     * @param key 缓存键
     * @return 是否成功
     */
    @Override
    public Boolean del(CacheBizTypeEn bizType, String key) {
        checkKey(key);
        String cacheKey = buildKey(bizType, key);
        
        caffeineExpireCache.invalidate(cacheKey);
        log.debug("Caffeine缓存删除成功: key={}, bizType={}", cacheKey, bizType.getValue());
        
        return true;
    }

    /**
     * 校验缓存键
     * 
     * @param key 缓存键
     */
    private void checkKey(String key) {
        if (StringUtils.isEmpty(key)) {
            throw new BizException(ErrorCodeEn.COMMON_CACHE_KEY_EMPTY);
        }
    }

    /**
     * 构建完整的缓存键
     * 
     * @param bizType 业务类型
     * @param key 缓存键
     * @return 完整缓存键
     */
    private String buildKey(CacheBizTypeEn bizType, String key) {
        return bizType.getValue() + ":" + key;
    }

    /**
     * 缓存值对象
     * 包含实际值、业务类型和过期时间
     */
    @Data
    public static class CacheValue {
        /**
         * 缓存的实际值
         */
        private String value;
        
        /**
         * 业务类型
         */
        private String bizType;
        
        /**
         * 过期时间戳(毫秒),null表示永不过期
         */
        private Long expireTime;
    }
}

