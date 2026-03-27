package pub.developers.forum.common.support;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.util.StringUtils;

/**
 * @author Qiangqiang.Bian
 * @create 2020/10/29
 * @desc
 **/
public class AvatarUtil {

    private static final String GRAVATAR_URL = "https://sdn.geekzu.org/avatar/%s?d=retro";
    private static final String EMPTY_STRING = "";

    private AvatarUtil() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static String get(String avatar, String email) {
        if (StringUtils.hasText(avatar)) {
            return avatar;
        }
        return generateGravatarUrl(email);
    }

    private static String generateGravatarUrl(String email) {
        String emailToHash = StringUtils.hasText(email) ? email : EMPTY_STRING;
        String emailHash = DigestUtils.md5Hex(emailToHash);
        return String.format(GRAVATAR_URL, emailHash);
    }

}