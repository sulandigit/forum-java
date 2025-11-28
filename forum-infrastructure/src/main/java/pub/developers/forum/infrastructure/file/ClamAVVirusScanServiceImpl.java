package pub.developers.forum.infrastructure.file;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import pub.developers.forum.common.support.LogUtil;
import pub.developers.forum.domain.service.VirusScanService;
import xyz.capybara.clamav.ClamavClient;
import xyz.capybara.clamav.commands.scan.result.ScanResult;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;

/**
 * ClamAV病毒扫描服务实现
 *
 * @author Forum Team
 * @create 2025/11/28
 */
@Slf4j
@Data
@ConfigurationProperties(prefix = "custom-config.upload-file.virus-scan")
@Component
public class ClamAVVirusScanServiceImpl implements VirusScanService {

    private Boolean enabled = true;
    private String host = "localhost";
    private Integer port = 3310;
    private Integer timeout = 30;

    private ClamavClient clamavClient;

    @PostConstruct
    public void init() {
        if (enabled) {
            try {
                clamavClient = new ClamavClient(host, port, timeout * 1000);
                log.info("ClamAV病毒扫描服务初始化成功, host={}, port={}, timeout={}s", host, port, timeout);
            } catch (Exception e) {
                log.error("ClamAV病毒扫描服务初始化失败", e);
            }
        } else {
            log.warn("病毒扫描服务已禁用，文件上传将跳过病毒扫描");
        }
    }

    @Override
    public boolean scanFile(byte[] fileBytes, String fileName) {
        if (!enabled) {
            log.warn("病毒扫描服务已禁用, fileName={}", fileName);
            return true;
        }

        if (clamavClient == null) {
            log.error("ClamAV客户端未初始化, fileName={}", fileName);
            return false;
        }

        try {
            long startTime = System.currentTimeMillis();
            ScanResult scanResult = clamavClient.scan(new ByteArrayInputStream(fileBytes));
            long duration = System.currentTimeMillis() - startTime;

            if (scanResult instanceof ScanResult.OK) {
                log.info("文件病毒扫描通过, fileName={}, duration={}ms", fileName, duration);
                return true;
            } else if (scanResult instanceof ScanResult.VirusFound) {
                ScanResult.VirusFound virusFound = (ScanResult.VirusFound) scanResult;
                log.error("检测到病毒, fileName={}, virus={}, duration={}ms",
                        fileName, virusFound.getFoundViruses(), duration);
                return false;
            } else {
                log.error("病毒扫描结果未知, fileName={}, result={}", fileName, scanResult);
                return false;
            }
        } catch (Exception e) {
            LogUtil.error(log, e, "病毒扫描异常, fileName={}", fileName);
            return false;
        }
    }

    @Override
    public boolean isAvailable() {
        if (!enabled) {
            return false;
        }

        if (clamavClient == null) {
            return false;
        }

        try {
            clamavClient.ping();
            return true;
        } catch (Exception e) {
            LogUtil.error(log, e, "ClamAV服务不可用");
            return false;
        }
    }
}
