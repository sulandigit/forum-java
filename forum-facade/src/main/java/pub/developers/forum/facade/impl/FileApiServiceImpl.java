package pub.developers.forum.facade.impl;

import org.springframework.stereotype.Service;
import pub.developers.forum.api.model.ResultModel;
import pub.developers.forum.api.request.file.FileUploadImgRequest;
import pub.developers.forum.api.service.FileApiService;
import pub.developers.forum.app.manager.FileManager;
import pub.developers.forum.facade.support.ResultModelUtil;
import pub.developers.forum.facade.validator.FileValidator;

import javax.annotation.Resource;

/**
 * File API Service Implementation
 * Handles file upload operations, particularly image uploads
 * 
 * @author Qiangqiang.Bian
 * @create 2020/11/23
 **/
@Service
public class FileApiServiceImpl implements FileApiService {

    @Resource
    private FileManager fileManager;

    /**
     * Upload an image file
     * Validates and uploads image to the configured storage system
     * 
     * @param request image upload request containing file data
     * @return URL or path to the uploaded image
     */
    @Override
    public ResultModel<String> uploadImg(FileUploadImgRequest request) {
        FileValidator.uploadImg(request);

        return ResultModelUtil.success(fileManager.uploadImg(request));
    }
}