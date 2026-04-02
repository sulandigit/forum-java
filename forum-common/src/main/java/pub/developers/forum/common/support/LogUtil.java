package pub.developers.forum.common.support;

import org.slf4j.Logger;
import org.slf4j.helpers.MessageFormatter;

public class LogUtil {

    private static final String TRACE_ID_PREFIX = "[traceId-";
    private static final String TRACE_ID_SUFFIX = "] - ";

    public static void info(Logger logger, String format, Object... args) {
        if (logger.isInfoEnabled()) {
            logger.info(getMsg(format, args));
        }
    }

    public static void info(Logger logger, Throwable throwable, String format, Object... args) {
        if (logger.isInfoEnabled()) {
            logger.info(getMsg(format, args), throwable);
        }
    }

    public static void warn(Logger logger, String format, Object... args) {
        if (logger.isWarnEnabled()) {
            logger.warn(getMsg(format, args));
        }
    }

    public static void warn(Logger logger, Throwable throwable, String format, Object... args) {
        if (logger.isWarnEnabled()) {
            logger.warn(getMsg(format, args), throwable);
        }
    }

    public static void error(Logger logger, String format, Object... args) {
        if (logger.isErrorEnabled()) {
            logger.error(getMsg(format, args));
        }
    }

    public static void error(Logger logger, Throwable throwable, String format, Object... args) {
        if (logger.isErrorEnabled()) {
            logger.error(getMsg(format, args), throwable);
        }
    }

    public static void debug(Logger logger, String format, Object... args) {
        if (logger.isDebugEnabled()) {
            logger.debug(getMsg(format, args));
        }
    }

    private static String getMsg(String format, Object... arguments) {
        StringBuilder sb = new StringBuilder(256);
        sb.append(TRACE_ID_PREFIX)
          .append(RequestContext.getTraceId())
          .append(TRACE_ID_SUFFIX);

        if (arguments != null && arguments.length > 0) {
            sb.append(MessageFormatter.arrayFormat(format, arguments).getMessage());
        } else {
            sb.append(format);
        }

        return sb.toString();
    }
}