package pub.developers.forum.common.support;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 搜索索引异步执行器服务
 * 负责管理搜索索引专用线程池和任务队列
 * 
 * @author System
 * @create 2025/11/14
 */
@Slf4j
@Component
public class SearchIndexExecutorService {

    /**
     * 搜索索引专用线程池
     */
    private final ThreadPoolExecutor executor;

    /**
     * 任务提交计数器
     */
    private final AtomicInteger taskCounter = new AtomicInteger(0);

    /**
     * 任务成功计数器
     */
    private final AtomicInteger successCounter = new AtomicInteger(0);

    /**
     * 任务失败计数器
     */
    private final AtomicInteger failureCounter = new AtomicInteger(0);

    public SearchIndexExecutorService() {
        // 创建线程池：核心线程2，最大线程4，队列容量10000
        this.executor = new ThreadPoolExecutor(
                2,  // 核心线程数
                4,  // 最大线程数
                60L,  // 线程存活时间
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(10000),  // 任务队列
                new NamedThreadFactory("search-index-async"),
                new ThreadPoolExecutor.CallerRunsPolicy()  // 拒绝策略
        );

        log.info("SearchIndexExecutorService initialized: corePoolSize=2, maxPoolSize=4, queueCapacity=10000");
    }

    /**
     * 提交搜索索引任务
     * 
     * @param task 索引任务
     */
    public void submitTask(Runnable task) {
        int taskId = taskCounter.incrementAndGet();
        
        executor.execute(() -> {
            long startTime = System.currentTimeMillis();
            try {
                log.info("[search-index-async] 搜索索引任务开始执行 - 任务ID:{}", taskId);
                task.run();
                
                long duration = System.currentTimeMillis() - startTime;
                successCounter.incrementAndGet();
                log.info("[search-index-async] 搜索索引任务执行成功 - 任务ID:{}, 耗时:{}ms", taskId, duration);
            } catch (Exception e) {
                long duration = System.currentTimeMillis() - startTime;
                failureCounter.incrementAndGet();
                log.error("[search-index-async] 搜索索引任务执行失败 - 任务ID:{}, 耗时:{}ms", taskId, duration, e);
            }
        });
        
        log.debug("[search-index-async] 搜索索引任务已提交 - 任务ID:{}, 队列大小:{}, 活跃线程:{}", 
                taskId, executor.getQueue().size(), executor.getActiveCount());
    }

    /**
     * 获取线程池统计信息
     */
    public String getStatistics() {
        return String.format(
                "SearchIndexExecutor[提交:%d, 成功:%d, 失败:%d, 队列:%d, 活跃线程:%d/%d]",
                taskCounter.get(),
                successCounter.get(),
                failureCounter.get(),
                executor.getQueue().size(),
                executor.getActiveCount(),
                executor.getMaximumPoolSize()
        );
    }

    /**
     * 优雅停机
     */
    @PreDestroy
    public void shutdown() {
        log.info("SearchIndexExecutorService shutting down... {}", getStatistics());
        
        executor.shutdown();
        
        try {
            // 等待30秒让任务完成
            if (!executor.awaitTermination(30, TimeUnit.SECONDS)) {
                log.warn("SearchIndexExecutorService 等待超时，强制关闭，未完成任务数: {}", executor.getQueue().size());
                executor.shutdownNow();
                
                // 再等待10秒
                if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
                    log.error("SearchIndexExecutorService 无法正常关闭");
                }
            } else {
                log.info("SearchIndexExecutorService 已优雅关闭");
            }
        } catch (InterruptedException e) {
            log.error("SearchIndexExecutorService 关闭过程被中断", e);
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    /**
     * 自定义线程工厂
     */
    private static class NamedThreadFactory implements ThreadFactory {
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;

        NamedThreadFactory(String namePrefix) {
            this.namePrefix = namePrefix;
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r, namePrefix + "-" + threadNumber.getAndIncrement());
            t.setDaemon(false);
            if (t.getPriority() != Thread.NORM_PRIORITY) {
                t.setPriority(Thread.NORM_PRIORITY);
            }
            return t;
        }
    }
}
