package pub.developers.forum.common.support;

import org.jasypt.encryption.StringEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EncryptionUtil {

    @Autowired
    private StringEncryptor stringEncryptor;

    /**
     * 加密字符串
     *
     * @param plainText 明文
     * @return 加密后的字符串
     */
    public String encrypt(String plainText) {
        return stringEncryptor.encrypt(plainText);
    }

    /**
     * 解密字符串
     *
     * @param encryptedText 密文
     * @return 解密后的明文
     */
    public String decrypt(String encryptedText) {
        return stringEncryptor.decrypt(encryptedText);
    }
}