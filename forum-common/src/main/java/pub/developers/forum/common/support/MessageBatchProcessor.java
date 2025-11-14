package pub.developers.forum.common.support;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pub.developers.forum.domain.entity.Message;
import pub.developers.forum.domain.repository.MessageRepository;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * 消息批量处理器
 * 负责站内信的批量保存，减少数据库操作频率
 * 
 * @author System
 * @create 2025/11/14
 */
@Slf4j
@Component
public class MessageBatchProcessor {

    @Resource
    private MessageRepository messageRepository;

    /**
     * 消息缓存队列
     */
    private final List<Message> messageBuffer = new ArrayList<>();

    /**
     * 批量大小阈值
     */
    private final int batchSize = 100;

    /**
     * 时间窗口（毫秒）
     */
    private final long batchTimeWindow = 1000L;

    /**
     * 定时刷新调度器
     */
    private final ScheduledExecutorService scheduler;

    /**
     * 最后一次刷新时间
     */
    private volatile long lastFlushTime = System.currentTimeMillis();

    /**
     * 锁对象
     */
    private final Object lock = new Object();

    public MessageBatchProcessor() {
        // 创建定时调度器，每500ms检查一次
        this.scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "message-batch-scheduler");
            t.setDaemon(true);
            return t;
        });

        // 启动定时刷新任务
        scheduler.scheduleAtFixedRate(this::checkAndFlush, 500, 500, TimeUnit.MILLISECONDS);
        
        log.info("MessageBatchProcessor initialized: batchSize={}, timeWindow={}ms", batchSize, batchTimeWindow);
    }

    /**
     * 添加消息到批量处理器
     * 
     * @param message 消息对象
     */
    public void addMessage(Message message) {
        synchronized (lock) {
            messageBuffer.add(message);
            log.debug("[消息批量] 消息已添加到缓冲区 - 当前缓冲数:{}", messageBuffer.size());

            // 如果达到批量大小，立即刷新
            if (messageBuffer.size() >= batchSize) {
                flush();
            }
        }
    }

    /**
     * 检查并刷新
     */
    private void checkAndFlush() {
        synchronized (lock) {
            if (messageBuffer.isEmpty()) {
                return;
            }

            // 超过时间窗口，强制刷新
            long now = System.currentTimeMillis();
            if (now - lastFlushTime >= batchTimeWindow) {
                flush();
            }
        }
    }

    /**
     * 刷新缓存，批量保存消息
     */
    private void flush() {
        if (messageBuffer.isEmpty()) {
            return;
        }

        List<Message> toSave = new ArrayList<>(messageBuffer);
        messageBuffer.clear();
        lastFlushTime = System.currentTimeMillis();

        try {
            long startTime = System.currentTimeMillis();
            // 批量保存
            batchSave(toSave);
            long duration = System.currentTimeMillis() - startTime;
            log.info("[消息批量] 批量保存成功 - 数量:{}, 耗时:{}ms", toSave.size(), duration);
        } catch (Exception e) {
            log.error("[消息批量] 批量保存失败 - 数量:{}", toSave.size(), e);
            // 失败时回退到逐个保存
            fallbackSave(toSave);
        }
    }

    /**
     * 批量保存消息
     */
    private void batchSave(List<Message> messages) {
        // 调用 MessageRepository 的批量保存接口
        messageRepository.batchSave(messages);
    }

    /**
     * 降级方案：逐个保存
     */
    private void fallbackSave(List<Message> messages) {
        log.warn("[消息批量] 使用降级方案，逐个保存消息");
        for (Message message : messages) {
            try {
                messageRepository.save(message);
            } catch (Exception e) {
                log.error("[消息批量] 单条消息保存失败", e);
            }
        }
    }

    /**
     * 强制刷新所有缓存消息
     */
    public void forceFlush() {
        synchronized (lock) {
            log.info("[消息批量] 强制刷新缓存 - 待保存数量:{}", messageBuffer.size());
            flush();
        }
    }

    /**
     * 获取统计信息
     */
    public String getStatistics() {
        synchronized (lock) {
            return String.format("MessageBatchProcessor[缓冲:%d]", messageBuffer.size());
        }
    }

    /**
     * 优雅停机
     */
    @PreDestroy
    public void shutdown() {
        log.info("MessageBatchProcessor shutting down... {}", getStatistics());
        
        // 强制刷新所有缓存
        forceFlush();
        
        // 关闭调度器
        scheduler.shutdown();
        
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
        
        log.info("MessageBatchProcessor 已优雅关闭");
    }
}
