package pub.developers.forum.common.support;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 异步处理配置类
 * 
 * @author Qiangqiang.Bian
 * @create 2025/11/14
 * @desc Spring异步任务配置
 */
@EnableAsync
@Configuration
public class AsyncConfig {

    /**
     * 自定义异步线程池
     * 
     * @return Executor
     */
    @Bean(name = "asyncExecutor")
    public Executor asyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        
        // 核心线程数
        executor.setCorePoolSize(10);
        
        // 最大线程数
        executor.setMaxPoolSize(20);
        
        // 队列容量
        executor.setQueueCapacity(200);
        
        // 线程名称前缀
        executor.setThreadNamePrefix("async-executor-");
        
        // 线程存活时间（秒）
        executor.setKeepAliveSeconds(60);
        
        // 拒绝策略：由调用线程处理该任务
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        
        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        
        // 等待时间（秒）
        executor.setAwaitTerminationSeconds(60);
        
        executor.initialize();
        
        return executor;
    }
}
