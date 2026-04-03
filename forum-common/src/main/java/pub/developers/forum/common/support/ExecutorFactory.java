package pub.developers.forum.common.support;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

@Slf4j
public class ExecutorFactory {

    private static final int DEFAULT_QUEUE_CAPACITY = 512;
    private static final long KEEP_ALIVE_TIME = 0L;

    private ExecutorFactory() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static ExecutorService getExecutorService(Class<?> cls, int fixedThreads) {
        if (cls == null) {
            throw new IllegalArgumentException("Class cannot be null");
        }
        if (fixedThreads <= 0) {
            throw new IllegalArgumentException("Thread count must be positive");
        }

        return new ThreadPoolExecutor(
                fixedThreads,
                fixedThreads,
                KEEP_ALIVE_TIME,
                TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(DEFAULT_QUEUE_CAPACITY),
                new ThreadFactoryBuilder()
                        .setNameFormat(cls.getSimpleName() + "-%d")
                        .setUncaughtExceptionHandler(getCommonHandler())
                        .setDaemon(false)
                        .build(),
                new ThreadPoolExecutor.AbortPolicy()
        );
    }

    private static Thread.UncaughtExceptionHandler getCommonHandler() {
        return (t, ex) -> {
            ThreadGroup threadGroup = t.getThreadGroup();
            if (threadGroup != null) {
                log.error("GroupName:[{}], ThreadName:[{}]", threadGroup.getName(), t.getName());
            } else {
                log.error("ThreadName:[{}]", t.getName());
            }

            if (ex != null) {
                log.error("Cause:[{}], Message:[{}]", ex.getCause(), ex.getMessage(), ex);
            }
        };
    }
}
