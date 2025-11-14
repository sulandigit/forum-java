package pub.developers.forum.common.support;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pub.developers.forum.common.enums.MessageChannelEn;
import pub.developers.forum.common.enums.MessageContentTypeEn;
import pub.developers.forum.domain.entity.Message;
import pub.developers.forum.domain.service.MailService;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 消息通知异步执行器服务
 * 负责管理消息通知专用线程池和任务队列
 * 集成批量处理器，支持站内信批量保存
 * 
 * @author System
 * @create 2025/11/14
 */
@Slf4j
@Component
public class MessageExecutorService {

    @Resource
    private MessageBatchProcessor messageBatchProcessor;

    @Resource
    private MailService mailService;

    /**
     * 消息通知专用线程池
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

    public MessageExecutorService() {
        // 创建线程池：核心线程4，最大线程8，队列容量50000
        this.executor = new ThreadPoolExecutor(
                4,  // 核心线程数
                8,  // 最大线程数
                60L,  // 线程存活时间
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(50000),  // 任务队列
                new NamedThreadFactory("message-async"),
                new ThreadPoolExecutor.CallerRunsPolicy()  // 拒绝策略
        );

        log.info("MessageExecutorService initialized: corePoolSize=4, maxPoolSize=8, queueCapacity=50000");
    }

    /**
     * 提交消息发送任务
     * 
     * @param message 消息对象
     */
    public void submitMessageTask(Message message) {
        int taskId = taskCounter.incrementAndGet();
        
        executor.execute(() -> {
            long startTime = System.currentTimeMillis();
            try {
                log.info("[message-async] 消息任务开始执行 - 任务ID:{}, 渠道:{}, 类型:{}", 
                        taskId, message.getChannel(), message.getType());
                
                processMessage(message);
                
                long duration = System.currentTimeMillis() - startTime;
                successCounter.incrementAndGet();
                log.info("[message-async] 消息任务执行成功 - 任务ID:{}, 耗时:{}ms", taskId, duration);
            } catch (Exception e) {
                long duration = System.currentTimeMillis() - startTime;
                failureCounter.incrementAndGet();
                log.error("[message-async] 消息任务执行失败 - 任务ID:{}, 耗时:{}ms", taskId, duration, e);
            }
        });
        
        log.debug("[message-async] 消息任务已提交 - 任务ID:{}, 队列大小:{}, 活跃线程:{}", 
                taskId, executor.getQueue().size(), executor.getActiveCount());
    }

    /**
     * 处理消息
     */
    private void processMessage(Message message) {
        // 根据渠道处理
        if (MessageChannelEn.MAIL.equals(message.getChannel())) {
            // 邮件：直接发送
            processMailMessage(message);
        } else if (MessageChannelEn.STATION_LETTER.equals(message.getChannel())) {
            // 站内信：提交到批量处理器
            processStationLetterMessage(message);
        }
    }

    /**
     * 处理邮件消息
     */
    private void processMailMessage(Message message) {
        log.info("[消息通知] 发送邮件 - 接收人:{}, 标题:{}", 
                message.getReceiver() != null ? message.getReceiver().getId() : "未知", 
                message.getTitle());
        
        if (MessageContentTypeEn.HTML.equals(message.getContentType())) {
            mailService.sendHtml(message);
        } else if (MessageContentTypeEn.TEXT.equals(message.getContentType())) {
            mailService.sendText(message);
        }
        
        // 邮件发送后也需要保存记录，这里不使用批量处理
        // messageRepository.save(message); 
        // 注意：需要在MessageServiceImpl中调用save
    }

    /**
     * 处理站内信消息
     */
    private void processStationLetterMessage(Message message) {
        log.info("[消息通知] 站内信添加到批量处理器 - 接收人:{}, 类型:{}", 
                message.getReceiver() != null ? message.getReceiver().getId() : "未知", 
                message.getType());
        
        // 提交到批量处理器
        messageBatchProcessor.addMessage(message);
    }

    /**
     * 获取线程池统计信息
     */
    public String getStatistics() {
        return String.format(
                "MessageExecutor[提交:%d, 成功:%d, 失败:%d, 队列:%d, 活跃线程:%d/%d, %s]",
                taskCounter.get(),
                successCounter.get(),
                failureCounter.get(),
                executor.getQueue().size(),
                executor.getActiveCount(),
                executor.getMaximumPoolSize(),
                messageBatchProcessor.getStatistics()
        );
    }

    /**
     * 优雅停机
     */
    @PreDestroy
    public void shutdown() {
        log.info("MessageExecutorService shutting down... {}", getStatistics());
        
        executor.shutdown();
        
        try {
            // 等待30秒让任务完成
            if (!executor.awaitTermination(30, TimeUnit.SECONDS)) {
                log.warn("MessageExecutorService 等待超时，强制关闭，未完成任务数: {}", executor.getQueue().size());
                executor.shutdownNow();
                
                // 再等待10秒
                if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
                    log.error("MessageExecutorService 无法正常关闭");
                }
            } else {
                log.info("MessageExecutorService 已优雅关闭");
            }
        } catch (InterruptedException e) {
            log.error("MessageExecutorService 关闭过程被中断", e);
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
