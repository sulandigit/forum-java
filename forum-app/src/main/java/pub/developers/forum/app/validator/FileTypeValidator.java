package pub.developers.forum.app.validator;

import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import pub.developers.forum.common.enums.ErrorCodeEn;
import pub.developers.forum.common.support.CheckUtil;
import pub.developers.forum.common.support.StringUtil;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 文件类型校验器
 * 通过扩展名、MIME类型、文件魔数进行多重校验
 */
@Slf4j
@Component
public class FileTypeValidator {

    @Value("${custom-config.upload-file.allowed-extensions:png,jpg,jpeg,gif,bmp,svg,ico}")
    private String allowedExtensions;

    @Value("${custom-config.upload-file.allowed-mime-types:image/png,image/jpeg,image/gif,image/bmp,image/svg+xml,image/x-icon}")
    private String allowedMimeTypes;

    /**
     * 文件魔数映射表
     */
    private static final Map<String, byte[][]> FILE_MAGIC_NUMBERS = new HashMap<>();

    static {
        // PNG: 89 50 4E 47 0D 0A 1A 0A
        FILE_MAGIC_NUMBERS.put("png", new byte[][]{
                {(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A}
        });

        // JPEG: FF D8 FF
        FILE_MAGIC_NUMBERS.put("jpg", new byte[][]{
                {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF}
        });
        FILE_MAGIC_NUMBERS.put("jpeg", new byte[][]{
                {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF}
        });

        // GIF: 47 49 46 38 (GIF8)
        FILE_MAGIC_NUMBERS.put("gif", new byte[][]{
                {0x47, 0x49, 0x46, 0x38}
        });

        // BMP: 42 4D
        FILE_MAGIC_NUMBERS.put("bmp", new byte[][]{
                {0x42, 0x4D}
        });

        // ICO: 00 00 01 00
        FILE_MAGIC_NUMBERS.put("ico", new byte[][]{
                {0x00, 0x00, 0x01, 0x00}
        });

        // SVG: 3C 73 76 67 (<svg) 或 3C 3F 78 6D 6C (<?xml)
        FILE_MAGIC_NUMBERS.put("svg", new byte[][]{
                {0x3C, 0x73, 0x76, 0x67}, // <svg
                {0x3C, 0x3F, 0x78, 0x6D, 0x6C} // <?xml
        });
    }

    /**
     * 校验文件类型
     *
     * @param originalFileName 原始文件名
     * @param contentType      MIME类型
     * @param fileBytes        文件字节数组
     */
    public void validate(String originalFileName, String contentType, byte[] fileBytes) {
        // 1. 扩展名校验
        String extension = getFileExtension(originalFileName);
        validateExtension(extension);

        // 2. MIME类型校验
        validateMimeType(contentType);

        // 3. 文件魔数校验
        validateMagicNumber(extension, fileBytes);

        log.info("文件类型校验通过, fileName={}, extension={}, contentType={}", originalFileName, extension, contentType);
    }

    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String fileName) {
        if (StringUtil.isEmpty(fileName)) {
            CheckUtil.isTrue(true, ErrorCodeEn.FILE_UPLOAD_NOT_SUPPORT_IMG_TYPE);
        }

        int lastDotIndex = fileName.lastIndexOf(".");
        if (lastDotIndex == -1 || lastDotIndex == fileName.length() - 1) {
            CheckUtil.isTrue(true, ErrorCodeEn.FILE_UPLOAD_NOT_SUPPORT_IMG_TYPE);
        }

        return fileName.substring(lastDotIndex + 1).toLowerCase();
    }

    /**
     * 校验扩展名
     */
    private void validateExtension(String extension) {
        Set<String> allowedExtSet = Sets.newHashSet(allowedExtensions.split(","));
        if (!allowedExtSet.contains(extension)) {
            log.warn("文件扩展名不在白名单中, extension={}, allowed={}", extension, allowedExtensions);
            CheckUtil.isTrue(true, ErrorCodeEn.FILE_UPLOAD_NOT_SUPPORT_IMG_TYPE);
        }
    }

    /**
     * 校验MIME类型
     */
    private void validateMimeType(String contentType) {
        if (StringUtil.isEmpty(contentType)) {
            log.warn("文件MIME类型为空");
            CheckUtil.isTrue(true, ErrorCodeEn.FILE_UPLOAD_NOT_SUPPORT_IMG_TYPE);
        }

        Set<String> allowedMimeSet = Sets.newHashSet(allowedMimeTypes.split(","));
        boolean isValid = false;
        for (String allowedMime : allowedMimeSet) {
            if (contentType.contains(allowedMime.trim())) {
                isValid = true;
                break;
            }
        }

        if (!isValid) {
            log.warn("文件MIME类型不在白名单中, contentType={}, allowed={}", contentType, allowedMimeTypes);
            CheckUtil.isTrue(true, ErrorCodeEn.FILE_UPLOAD_NOT_SUPPORT_IMG_TYPE);
        }
    }

    /**
     * 校验文件魔数
     */
    private void validateMagicNumber(String extension, byte[] fileBytes) {
        if (fileBytes == null || fileBytes.length == 0) {
            log.warn("文件内容为空，无法进行魔数校验");
            CheckUtil.isTrue(true, ErrorCodeEn.FILE_CONTENT_TYPE_MISMATCH);
        }

        byte[][] magicNumbers = FILE_MAGIC_NUMBERS.get(extension);
        if (magicNumbers == null) {
            // 如果没有定义魔数，仅依赖扩展名和MIME类型校验
            log.warn("未定义文件魔数映射, extension={}, 跳过魔数校验", extension);
            return;
        }

        boolean matched = false;
        for (byte[] magicNumber : magicNumbers) {
            if (matchesMagicNumber(fileBytes, magicNumber)) {
                matched = true;
                break;
            }
        }

        if (!matched) {
            log.warn("文件魔数校验失败, extension={}, fileSize={}", extension, fileBytes.length);
            CheckUtil.isTrue(true, ErrorCodeEn.FILE_CONTENT_TYPE_MISMATCH);
        }
    }

    /**
     * 判断文件字节是否匹配魔数
     */
    private boolean matchesMagicNumber(byte[] fileBytes, byte[] magicNumber) {
        if (fileBytes.length < magicNumber.length) {
            return false;
        }

        for (int i = 0; i < magicNumber.length; i++) {
            if (fileBytes[i] != magicNumber[i]) {
                return false;
            }
        }

        return true;
    }
}
