package pub.developers.forum.app.validator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import pub.developers.forum.common.enums.ErrorCodeEn;
import pub.developers.forum.common.support.CheckUtil;

/**
 * 文件大小校验器
 */
@Slf4j
@Component
public class FileSizeValidator {

    @Value("${custom-config.upload-file.max-file-size:10}")
    private Integer maxFileSizeMB;

    /**
     * 校验文件大小
     *
     * @param fileSize 文件大小（字节）
     * @param fileName 文件名（用于日志）
     */
    public void validate(Long fileSize, String fileName) {
        if (fileSize == null || fileSize <= 0) {
            log.warn("文件大小无效, fileName={}, fileSize={}", fileName, fileSize);
            CheckUtil.isTrue(true, ErrorCodeEn.FILE_UPLOAD_FAIL);
        }

        long maxFileSizeBytes = maxFileSizeMB * 1024L * 1024L;
        if (fileSize > maxFileSizeBytes) {
            log.warn("文件大小超过限制, fileName={}, fileSize={} bytes, maxSize={} MB",
                    fileName, fileSize, maxFileSizeMB);
            CheckUtil.isTrue(true, ErrorCodeEn.FILE_SIZE_EXCEED);
        }

        log.info("文件大小校验通过, fileName={}, fileSize={} bytes, maxSize={} MB",
                fileName, fileSize, maxFileSizeMB);
    }
}
