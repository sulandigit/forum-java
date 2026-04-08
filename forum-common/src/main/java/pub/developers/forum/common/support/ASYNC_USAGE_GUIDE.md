# Spring @Async 异步处理使用指南

## 一、配置说明

### 1. 异步配置类
已在 `AsyncConfig.java` 中配置了异步线程池，配置详情如下：

```java
@EnableAsync
@Configuration
public class AsyncConfig {
    @Bean(name = "asyncExecutor")
    public Executor asyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);           // 核心线程数
        executor.setMaxPoolSize(20);            // 最大线程数
        executor.setQueueCapacity(200);         // 队列容量
        executor.setThreadNamePrefix("async-executor-");
        executor.setKeepAliveSeconds(60);
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }
}
```

### 2. 线程池参数说明

| 参数 | 值 | 说明 |
|-----|---|-----|
| CorePoolSize | 10 | 核心线程数，始终保持活动 |
| MaxPoolSize | 20 | 最大线程数，高峰期可扩展到此数量 |
| QueueCapacity | 200 | 任务队列容量 |
| KeepAliveSeconds | 60 | 空闲线程存活时间（秒） |
| RejectedExecutionHandler | CallerRunsPolicy | 拒绝策略：由调用线程处理任务 |

## 二、使用方法

### 1. 在服务方法上添加 @Async 注解

#### 无返回值的异步方法
```java
@Service
public class YourService {
    
    @Async("asyncExecutor")
    public void processAsync(String data) {
        // 异步执行的业务逻辑
        log.info("异步处理数据: {}, 线程: {}", data, Thread.currentThread().getName());
    }
}
```

#### 有返回值的异步方法
```java
@Service
public class YourService {
    
    @Async("asyncExecutor")
    public Future<String> processAsyncWithResult(String param) {
        // 异步执行的业务逻辑
        String result = doSomething(param);
        return new AsyncResult<>(result);
    }
}
```

### 2. 调用异步方法

```java
@Service
public class CallerService {
    
    @Resource
    private YourService yourService;
    
    public void callAsyncMethod() {
        // 无返回值的异步调用
        yourService.processAsync("test data");
        log.info("异步方法已提交，主线程继续执行");
        
        // 有返回值的异步调用
        Future<String> future = yourService.processAsyncWithResult("param");
        try {
            // 获取异步执行结果（会阻塞直到结果返回）
            String result = future.get(5, TimeUnit.SECONDS);
            log.info("异步执行结果: {}", result);
        } catch (Exception e) {
            log.error("获取异步结果失败", e);
        }
    }
}
```

## 三、实际应用示例

### 1. 异步发送邮件（已实现）
在 `Mail163ServiceImpl` 中，邮件发送方法已改为异步执行：

```java
@Async("asyncExecutor")
@Override
public void sendHtml(Message mailMessage) {
    log.info("异步发送HTML邮件开始, 收件人: {}", mailMessage.getReceiver().getId());
    // 发送邮件的具体逻辑
}
```

### 2. 异步通知示例
```java
@Service
public class NotificationService {
    
    @Async("asyncExecutor")
    public void sendNotification(Long userId, String message) {
        // 发送通知的逻辑
        log.info("异步发送通知给用户: {}", userId);
    }
}
```

### 3. 异步日志处理示例
```java
@Service
public class LogService {
    
    @Async("asyncExecutor")
    public void saveLog(String logContent) {
        // 保存日志到数据库或日志中心
        log.info("异步处理日志");
    }
}
```

## 四、注意事项

### ⚠️ 重要提醒

1. **避免自调用**
   - 异步方法必须通过 Spring 代理对象调用才能生效
   - 在同一个类中直接调用异步方法不会异步执行
   ```java
   // ❌ 错误：不会异步执行
   public void methodA() {
       this.asyncMethod();
   }
   
   // ✅ 正确：通过注入的 Bean 调用
   @Resource
   private YourService yourService;
   public void methodA() {
       yourService.asyncMethod();
   }
   ```

2. **异常处理**
   - 异步方法中的异常不会传播到调用方
   - 建议在异步方法内部进行完整的异常处理和日志记录
   ```java
   @Async("asyncExecutor")
   public void asyncMethod() {
       try {
           // 业务逻辑
       } catch (Exception e) {
           log.error("异步方法执行异常", e);
           // 异常处理逻辑
       }
   }
   ```

3. **事务处理**
   - 异步方法中的事务是独立的
   - 调用方的事务不会传播到异步方法中
   - 如需事务，在异步方法上添加 `@Transactional` 注解

4. **返回值类型**
   - `void`：无返回值
   - `Future<T>`：可获取异步执行结果
   - `CompletableFuture<T>`：支持更丰富的异步编程（推荐）

5. **线程池选择**
   - 可以不指定线程池：`@Async`（使用默认线程池）
   - 推荐指定线程池：`@Async("asyncExecutor")`（使用自定义线程池）

## 五、性能优化建议

1. **合理设置线程池参数**
   - 根据实际业务量调整核心线程数和最大线程数
   - CPU 密集型任务：线程数 = CPU 核数 + 1
   - IO 密集型任务：线程数 = CPU 核数 * 2

2. **监控线程池状态**
   - 定期检查线程池活动线程数
   - 监控任务队列长度
   - 关注任务拒绝次数

3. **避免滥用异步**
   - 不是所有方法都适合异步执行
   - 简单快速的操作无需异步
   - 耗时的 IO 操作、第三方接口调用适合异步

## 六、示例代码参考

完整示例请参考：
- `AsyncConfig.java` - 异步配置
- `AsyncExampleService.java` - 使用示例
- `Mail163ServiceImpl.java` - 实际应用（异步发送邮件）

## 七、常见问题排查

### Q1: 异步方法没有生效？
- 检查是否添加了 `@EnableAsync` 注解
- 检查是否通过 Spring 容器调用（而非 this 调用）
- 检查方法是否为 public

### Q2: 线程池满了怎么办？
- 检查 `RejectedExecutionHandler` 配置
- 当前配置为 `CallerRunsPolicy`，会由调用线程执行任务
- 可根据业务需求修改拒绝策略

### Q3: 如何确认方法是否异步执行？
- 查看日志中的线程名称
- 异步执行的线程名会包含 "async-executor-" 前缀
- 可在方法中打印 `Thread.currentThread().getName()`

---

**更新时间**: 2025-11-14  
**维护者**: Qiangqiang.Bian
