package pub.developers.forum.infrastructure.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * Caffeine缓存配置类
 * 
 * @author forum-java
 * @create 2025/11/14
 * @desc Caffeine本地缓存配置
 */
@Configuration
@ConditionalOnProperty(name = "cache.type", havingValue = "caffeine")
public class CaffeineConfig {

    /**
     * 创建Caffeine缓存实例
     * 配置缓存的最大容量、过期时间等参数
     * 
     * @return Cache实例
     */
    @Bean
    public Cache<String, Object> caffeineCache() {
        return Caffeine.newBuilder()
                // 初始缓存空间大小
                .initialCapacity(100)
                // 缓存的最大容量
                .maximumSize(10000)
                // 写入后过期时间(默认30分钟)
                .expireAfterWrite(30, TimeUnit.MINUTES)
                // 访问后过期时间(默认10分钟)
                .expireAfterAccess(10, TimeUnit.MINUTES)
                // 开启缓存统计
                .recordStats()
                .build();
    }

    /**
     * 创建带有自定义过期时间的缓存实例
     * 用于需要特定过期时间的缓存场景
     * 
     * @return Cache实例
     */
    @Bean
    public Cache<String, CaffeineCacheServiceImpl.CacheValue> caffeineExpireCache() {
        return Caffeine.newBuilder()
                .initialCapacity(100)
                .maximumSize(10000)
                // 使用expireAfter自定义过期策略
                .expireAfter(new CaffeineExpirePolicy())
                .recordStats()
                .build();
    }
}
