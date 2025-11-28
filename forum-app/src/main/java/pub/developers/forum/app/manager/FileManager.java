package pub.developers.forum.app.manager;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pub.developers.forum.api.request.file.FileUploadImgRequest;
import pub.developers.forum.app.support.IsLogin;
import pub.developers.forum.app.validator.VirusScanValidator;
import pub.developers.forum.domain.service.FileService;

import javax.annotation.Resource;

/**
 * @author Qiangqiang.Bian
 * @create 2020/11/23
 * @desc
 **/
@Slf4j
@Component
public class FileManager {

    @Resource
    private FileService fileService;

    @Resource
    private VirusScanValidator virusScanValidator;

    @IsLogin
    public String uploadImg(FileUploadImgRequest request) {
        // 病毒扫描校验
        virusScanValidator.validate(request.getBase64(), request.getOriginalFileName());

        log.info("文件上传所有校验通过，开始上传到七牛云, fileName={}", request.getOriginalFileName());
        return fileService.uploadImg(request.getBase64(), request.getFileName());
    }
}
