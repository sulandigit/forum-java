package pub.developers.forum.common.support;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import org.apache.commons.codec.digest.DigestUtils;

import java.nio.charset.Charset;
import java.util.UUID;

/**
 * @author Qiangqiang.Bian
 * @create 20/7/23
 * @desc
 **/
public class StringUtil {
    private StringUtil() {
    }

    private static final String MD5_PRE_KEY = "1357924680QWERqwer";
    private static final String MD5_POST_KEY = "0987654321zxcv";
    private static final String[] COLOR_CODE = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F" };

    public static void main(String[] args) {
        System.out.println(getColor(MD5_POST_KEY));
    }

    /**
     * 获取16进制颜色值
     * @param str
     * @return
     */
    public static String getColor(String str) {
        int hash = str.hashCode();
        Integer[] rgb = new Integer[]{(hash & 0xFF0000) >> 16, (hash & 0x00FF00) >> 8, hash & 0x0000FF};

        StringBuilder hexCode = new StringBuilder("#");
        for (int i = 0; i < rgb.length; i ++) {
            int rgbItem = rgb[i];

            if (rgbItem < 0) {
                rgbItem = 0;
            } else if (rgbItem > 255){
                rgbItem = 255;
            }

            hexCode.append(COLOR_CODE[rgbItem / 16]).append(COLOR_CODE[rgbItem % 16]);
        }

        return hexCode.toString();
    }

    /**
     * 生成 uuid 字符串
     *
     * @return
     */
    public static String generateUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    /**
     * 对 value 进行 md5 加密
     * @param password
     * @return
     */
    public static String md5UserPassword(String password) {
        return md5(MD5_PRE_KEY + password + MD5_POST_KEY);
    }

    /**
     * 对 value 进行 md5 加密
     * @param value
     * @return
     */
    public static String md5(String value) {
        return DigestUtils.md5Hex(value);
    }

    /**
     * json string 属性值最大长度
     */
    private static final Integer MAX_LENGTH_FOR_PER_PROPERTY = 100;

    public static String toJSONString(Object result) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(new TruncateWrapper(result));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize object to JSON", e);
        }
    }

    private static class TruncateWrapper {
        private final Object value;

        TruncateWrapper(Object value) {
            this.value = truncateValue(value);
        }

        public Object getValue() {
            return value;
        }

        private Object truncateValue(Object val) {
            if (val instanceof String && ((String) val).length() > MAX_LENGTH_FOR_PER_PROPERTY) {
                return ((String) val).substring(0, MAX_LENGTH_FOR_PER_PROPERTY) + "...";
            }
            if (val instanceof byte[]) {
                String s = new String((byte[]) val, Charset.defaultCharset());
                if (s.length() > MAX_LENGTH_FOR_PER_PROPERTY) {
                    return s.substring(0, MAX_LENGTH_FOR_PER_PROPERTY) + "...";
                }
                return s;
            }
            return val;
        }
    }


}