package pub.developers.forum.common.support;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.Future;

/**
 * 异步处理示例服务
 * 
 * 使用说明：
 * 1. 在需要异步执行的方法上添加 @Async 注解
 * 2. 可以指定线程池名称，如 @Async("asyncExecutor")
 * 3. 异步方法可以返回 void 或 Future 类型
 * 4. 注意：异步方法必须通过 Spring 容器调用才能生效，不能在同一个类中直接调用
 * 
 * @author Qiangqiang.Bian
 * @create 2025/11/14
 * @desc Spring异步任务使用示例
 */
@Slf4j
@Service
public class AsyncExampleService {

    /**
     * 无返回值的异步方法
     * 
     * @param taskName 任务名称
     */
    @Async("asyncExecutor")
    public void executeAsyncTask(String taskName) {
        log.info("异步任务开始执行: {}, 线程: {}", taskName, Thread.currentThread().getName());
        
        try {
            // 模拟耗时操作
            Thread.sleep(2000);
            
            log.info("异步任务执行完成: {}", taskName);
        } catch (InterruptedException e) {
            log.error("异步任务执行异常: {}", taskName, e);
            Thread.currentThread().interrupt();
        }
    }

    /**
     * 有返回值的异步方法
     * 
     * @param param 参数
     * @return Future<String> 异步执行结果
     */
    @Async("asyncExecutor")
    public Future<String> executeAsyncTaskWithResult(String param) {
        log.info("带返回值的异步任务开始执行, 参数: {}, 线程: {}", param, Thread.currentThread().getName());
        
        try {
            // 模拟耗时操作
            Thread.sleep(3000);
            
            String result = "处理结果: " + param;
            log.info("带返回值的异步任务执行完成, 结果: {}", result);
            
            return new AsyncResult<>(result);
        } catch (InterruptedException e) {
            log.error("带返回值的异步任务执行异常, 参数: {}", param, e);
            Thread.currentThread().interrupt();
            return new AsyncResult<>("执行失败");
        }
    }

    /**
     * 异步发送通知
     * 
     * @param userId 用户ID
     * @param message 消息内容
     */
    @Async("asyncExecutor")
    public void sendNotificationAsync(Long userId, String message) {
        log.info("异步发送通知, 用户ID: {}, 消息: {}, 线程: {}", userId, message, Thread.currentThread().getName());
        
        try {
            // 模拟发送通知的耗时操作
            Thread.sleep(1000);
            
            // 这里可以调用邮件服务、短信服务等
            // mailService.send(...)
            // smsService.send(...)
            
            log.info("通知发送成功, 用户ID: {}", userId);
        } catch (InterruptedException e) {
            log.error("通知发送失败, 用户ID: {}", userId, e);
            Thread.currentThread().interrupt();
        }
    }

    /**
     * 异步处理日志
     * 
     * @param logContent 日志内容
     */
    @Async("asyncExecutor")
    public void processLogAsync(String logContent) {
        log.info("异步处理日志开始, 线程: {}", Thread.currentThread().getName());
        
        try {
            // 模拟日志处理操作（如写入数据库、发送到日志中心等）
            Thread.sleep(500);
            
            log.info("日志处理完成: {}", logContent);
        } catch (InterruptedException e) {
            log.error("日志处理异常", e);
            Thread.currentThread().interrupt();
        }
    }
}
