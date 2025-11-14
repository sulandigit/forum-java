package pub.developers.forum.infrastructure.cache;

import com.github.benmanes.caffeine.cache.Expiry;
import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Caffeine自定义过期策略
 * 
 * @author forum-java
 * @create 2025/11/14
 * @desc 根据缓存值中的过期时间动态设置过期策略
 */
public class CaffeineExpirePolicy implements Expiry<String, CaffeineCacheServiceImpl.CacheValue> {

    /**
     * 创建缓存时的过期时间
     * 
     * @param key 缓存键
     * @param value 缓存值
     * @param currentTime 当前时间(纳秒)
     * @return 过期时间(纳秒)
     */
    @Override
    public long expireAfterCreate(@NonNull String key, @NonNull CaffeineCacheServiceImpl.CacheValue value, long currentTime) {
        Long expireTime = value.getExpireTime();
        if (expireTime == null || expireTime <= 0) {
            // 永不过期,返回Long.MAX_VALUE
            return Long.MAX_VALUE;
        }
        // 返回剩余过期时间(纳秒)
        long remainingTime = (expireTime - System.currentTimeMillis()) * 1_000_000;
        return remainingTime > 0 ? remainingTime : 0;
    }

    /**
     * 更新缓存时的过期时间
     * 
     * @param key 缓存键
     * @param value 缓存值
     * @param currentTime 当前时间(纳秒)
     * @param currentDuration 当前持续时间(纳秒)
     * @return 过期时间(纳秒)
     */
    @Override
    public long expireAfterUpdate(@NonNull String key, @NonNull CaffeineCacheServiceImpl.CacheValue value, 
                                   long currentTime, @NonNegative long currentDuration) {
        Long expireTime = value.getExpireTime();
        if (expireTime == null || expireTime <= 0) {
            return Long.MAX_VALUE;
        }
        long remainingTime = (expireTime - System.currentTimeMillis()) * 1_000_000;
        return remainingTime > 0 ? remainingTime : 0;
    }

    /**
     * 读取缓存时的过期时间
     * 
     * @param key 缓存键
     * @param value 缓存值
     * @param currentTime 当前时间(纳秒)
     * @param currentDuration 当前持续时间(纳秒)
     * @return 过期时间(纳秒)
     */
    @Override
    public long expireAfterRead(@NonNull String key, @NonNull CaffeineCacheServiceImpl.CacheValue value, 
                                 long currentTime, @NonNegative long currentDuration) {
        // 读取时不改变过期时间
        return currentDuration;
    }
}
