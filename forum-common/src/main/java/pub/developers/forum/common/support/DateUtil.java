package pub.developers.forum.common.support;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Date utility class for common date formatting operations
 * 
 * @author Qiangqiang.Bian
 * @create 2020/12/8
 **/
public class DateUtil {

    /**
     * Formats a Date object to string in "yyyy-MM-dd HH:mm:ss" format
     * 
     * @param date the Date object to format
     * @return formatted date string in "yyyy-MM-dd HH:mm:ss" format
     */
    public static String toyyyyMMddHHmmss(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
    }
}