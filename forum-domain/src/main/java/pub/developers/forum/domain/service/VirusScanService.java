package pub.developers.forum.domain.service;

/**
 * 病毒扫描服务接口
 * 
 * @author Forum Team
 * @create 2025/11/28
 */
public interface VirusScanService {

    /**
     * 扫描文件是否包含病毒
     *
     * @param fileBytes 文件字节数组
     * @param fileName  文件名（用于日志）
     * @return true-安全，false-检测到病毒
     */
    boolean scanFile(byte[] fileBytes, String fileName);

    /**
     * 检查病毒扫描服务是否可用
     *
     * @return true-可用，false-不可用
     */
    boolean isAvailable();
}
