package pub.developers.forum.app.validator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pub.developers.forum.common.enums.ErrorCodeEn;
import pub.developers.forum.common.support.CheckUtil;
import pub.developers.forum.domain.service.VirusScanService;

import javax.annotation.Resource;

/**
 * 病毒扫描校验器
 */
@Slf4j
@Component
public class VirusScanValidator {

    @Resource
    private VirusScanService virusScanService;

    /**
     * 校验文件病毒扫描
     *
     * @param fileBytes 文件字节数组
     * @param fileName  文件名（用于日志）
     */
    public void validate(byte[] fileBytes, String fileName) {
        // 检查服务是否可用
        if (!virusScanService.isAvailable()) {
            log.error("病毒扫描服务不可用, fileName={}", fileName);
            CheckUtil.isTrue(true, ErrorCodeEn.FILE_SCAN_SERVICE_UNAVAILABLE);
        }

        // 执行病毒扫描
        boolean isSafe = virusScanService.scanFile(fileBytes, fileName);
        if (!isSafe) {
            log.error("文件病毒扫描未通过, fileName={}", fileName);
            CheckUtil.isTrue(true, ErrorCodeEn.FILE_VIRUS_DETECTED);
        }

        log.info("文件病毒扫描校验通过, fileName={}", fileName);
    }
}
