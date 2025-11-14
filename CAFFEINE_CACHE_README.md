# Caffeine本地缓存使用说明

## 一、功能简介

已成功为forum-java项目添加基于Caffeine的本地缓存实现。Caffeine是一款高性能的Java本地缓存库,提供了接近最优的命中率。

## 二、实现内容

### 1. 依赖添加

- **父POM** (`pom.xml`): 在 `dependencyManagement` 中添加了 Caffeine 2.8.8 版本依赖管理
- **Infrastructure模块** (`forum-infrastructure/pom.xml`): 添加了 Caffeine 依赖引用

### 2. 核心实现类

#### (1) CaffeineConfig.java
- **位置**: `forum-infrastructure/src/main/java/pub/developers/forum/infrastructure/cache/CaffeineConfig.java`
- **功能**: Caffeine缓存配置类
- **配置项**:
  - 初始容量: 100
  - 最大容量: 10000
  - 写入后过期: 30分钟
  - 访问后过期: 10分钟
  - 启用缓存统计
- **条件加载**: 仅当 `cache.type=caffeine` 时生效

#### (2) CaffeineExpirePolicy.java
- **位置**: `forum-infrastructure/src/main/java/pub/developers/forum/infrastructure/cache/CaffeineExpirePolicy.java`
- **功能**: 自定义过期策略
- **特性**: 支持为每个缓存项设置独立的过期时间

#### (3) CaffeineCacheServiceImpl.java
- **位置**: `forum-infrastructure/src/main/java/pub/developers/forum/infrastructure/cache/CaffeineCacheServiceImpl.java`
- **功能**: CacheService接口的Caffeine实现
- **实现方法**:
  - `set()`: 存储缓存(永久有效)
  - `setAndExpire()`: 存储缓存并设置过期时间
  - `get()`: 获取缓存值
  - `exists()`: 判断缓存是否存在
  - `del()`: 删除缓存
- **条件加载**: 仅当 `cache.type=caffeine` 时生效

#### (4) DbCacheServiceImpl.java (修改)
- **修改内容**: 添加 `@ConditionalOnProperty` 注解
- **条件加载**: 仅当 `cache.type=db` 或未配置时生效(默认使用数据库缓存)

### 3. 配置文件修改

**application.properties**:
```properties
#####################################################################################################
###################################### 缓存配置 ####################################################
#####################################################################################################
# 缓存类型: db(数据库缓存) 或 caffeine(本地缓存)
# cache.type=db
cache.type=caffeine
```

## 三、使用方式

### 1. 启用Caffeine缓存

在 `application.properties` 中设置:
```properties
cache.type=caffeine
```

### 2. 切换回数据库缓存

在 `application.properties` 中设置:
```properties
cache.type=db
```
或注释掉该配置项(默认使用数据库缓存)

### 3. 代码使用示例

缓存服务的使用方式保持不变,只需注入 `CacheService` 接口:

```java
@Resource
private CacheService cacheService;

// 存储缓存(永久有效)
cacheService.set(CacheBizTypeEn.USER, "userId_123", "userData");

// 存储缓存并设置过期时间(秒)
cacheService.setAndExpire(CacheBizTypeEn.USER, "userId_123", "userData", 3600L);

// 获取缓存
String value = cacheService.get(CacheBizTypeEn.USER, "userId_123");

// 判断缓存是否存在
Boolean exists = cacheService.exists(CacheBizTypeEn.USER, "userId_123");

// 删除缓存
cacheService.del(CacheBizTypeEn.USER, "userId_123");
```

## 四、优势特点

### Caffeine本地缓存优势:
1. **高性能**: 读写速度极快,无网络IO开销
2. **近乎最优的命中率**: 使用 Window TinyLFU 淘汰算法
3. **灵活的过期策略**: 支持基于时间、容量的多种过期策略
4. **线程安全**: 内部使用并发安全的数据结构
5. **统计功能**: 支持缓存命中率等统计信息
6. **零外部依赖**: 不依赖Redis等外部服务

### 与数据库缓存对比:
- **性能**: Caffeine > 数据库缓存
- **持久化**: 数据库缓存支持,Caffeine不支持(进程重启后丢失)
- **分布式**: 数据库缓存可跨实例共享,Caffeine仅本机可用
- **资源占用**: Caffeine占用内存,数据库缓存占用磁盘

## 五、注意事项

1. **内存管理**: Caffeine缓存存储在JVM堆内存中,需合理设置最大容量以避免OOM
2. **数据持久化**: Caffeine是内存缓存,应用重启后缓存数据会丢失
3. **分布式环境**: 在多实例部署时,各实例的Caffeine缓存独立,不共享数据
4. **适用场景**: 适合读多写少、数据量不大、允许丢失的缓存场景

## 六、配置参数说明

可根据实际业务需求,在 `CaffeineConfig.java` 中调整以下参数:

| 参数 | 说明 | 默认值 |
|-----|------|--------|
| initialCapacity | 初始缓存容量 | 100 |
| maximumSize | 最大缓存条目数 | 10000 |
| expireAfterWrite | 写入后过期时间 | 30分钟 |
| expireAfterAccess | 访问后过期时间 | 10分钟 |

## 七、扩展建议

如需更高级的功能,可考虑:
1. 添加缓存预热机制
2. 实现缓存与数据库的双写一致性
3. 添加缓存监控和报警
4. 实现二级缓存(Caffeine + Redis)
