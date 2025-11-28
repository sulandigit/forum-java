package pub.developers.forum.portal.controller.rest;

import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pub.developers.forum.api.model.ResultModel;
import pub.developers.forum.api.request.file.FileUploadImgRequest;
import pub.developers.forum.api.request.user.*;
import pub.developers.forum.api.service.FileApiService;
import pub.developers.forum.api.service.UserApiService;
import pub.developers.forum.app.validator.FileSizeValidator;
import pub.developers.forum.app.validator.FileTypeValidator;
import pub.developers.forum.common.constant.Constant;
import pub.developers.forum.common.enums.ErrorCodeEn;
import pub.developers.forum.common.support.CheckUtil;
import pub.developers.forum.portal.support.WebUtil;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

/**
 * @author Qiangqiang.Bian
 * @create 2020/10/29
 * @desc
 **/
@Slf4j
@RestController
@RequestMapping("/user-rest")
public class UserRestController {

    @Resource
    private UserApiService userApiService;

    @Resource
    private FileApiService fileApiService;

    @Resource
    private FileSizeValidator fileSizeValidator;

    @Resource
    private FileTypeValidator fileTypeValidator;

    // .css;.js;.png;.jpeg;.jpg;.woff2;.html;.ico;.gif;.bmp;.svg;.woff;.map
    private static final Set<String> ALLOW_TYPES = Sets.newHashSet("png", "jpeg", "jpg", "ico", "gif", "bmp", "svg");

    @PostMapping("/register")
    public ResultModel<String> register(@RequestBody UserRegisterRequest request, HttpServletRequest servletRequest, HttpServletResponse response) {
        request.setIp(WebUtil.requestIp(servletRequest));
        request.setUa(WebUtil.requestUa(servletRequest));
        ResultModel<String> resultModel = userApiService.register(request);

        WebUtil.cookieAddSid(response, resultModel.getData());

        return resultModel;
    }

    @PostMapping("/login")
    public ResultModel<String> login(@RequestBody UserEmailLoginRequest request, HttpServletRequest servletRequest, HttpServletResponse response) {
        request.setIp(WebUtil.requestIp(servletRequest));
        request.setUa(WebUtil.requestUa(servletRequest));
        ResultModel<String> resultModel =  userApiService.emailLogin(request);

        WebUtil.cookieAddSid(response, resultModel.getData());

        return resultModel;
    }

    @PostMapping("/update-info")
    public ResultModel updateInfo(@RequestBody UserUpdateInfoRequest updateInfoRequest, HttpServletRequest request) {
        request.setAttribute(Constant.REQUEST_HEADER_TOKEN_KEY, WebUtil.cookieGetSid(request));

        return userApiService.updateInfo(updateInfoRequest);
    }

    @PostMapping("/update-headimg")
    public ResultModel ResultModelupdateHeadImg(MultipartFile file, HttpServletRequest request)throws IOException {
        request.setAttribute(Constant.REQUEST_HEADER_TOKEN_KEY, WebUtil.cookieGetSid(request));
        //获取文件的内容
        InputStream is = file.getInputStream();
        //获取原始文件名
        String originalFilename = file.getOriginalFilename();
        ResultModel<String> linkFilename =  updateHeadimg(file,originalFilename,request);
        return userApiService.updateHeadImg(linkFilename.getData());
    }



    private ResultModel<String> updateHeadimg(MultipartFile file, String originalFilename, HttpServletRequest request) {
        request.setAttribute(Constant.REQUEST_HEADER_TOKEN_KEY, WebUtil.cookieGetSid(request));

        // 文件基本校验
        CheckUtil.checkParamToast(file, "file");
        CheckUtil.checkParamToast(originalFilename, "originalFileName");

        // 1. 文件大小校验
        fileSizeValidator.validate(file.getSize(), originalFilename);

        // 2. 文件类型校验（扩展名、MIME类型、魔数）
        FileUploadImgRequest uploadImgRequest = null;
        try {
            byte[] fileBytes = file.getBytes();
            fileTypeValidator.validate(originalFilename, file.getContentType(), fileBytes);

            uploadImgRequest = FileUploadImgRequest.builder()
                    .base64(fileBytes)
                    .fileName(originalFilename)
                    .originalFileName(originalFilename)
                    .fileSize(file.getSize())
                    .contentType(file.getContentType())
                    .build();
        } catch (Exception e) {
            log.error("文件上传处理异常, fileName={}", originalFilename, e);
            CheckUtil.isTrue(true, ErrorCodeEn.FILE_UPLOAD_FAIL);
        }

        return fileApiService.uploadImg(uploadImgRequest);
    }

    @PostMapping("/update-pwd")
    public ResultModel updatePwd(@RequestBody UserUpdatePwdRequest updatePwdRequest, HttpServletRequest request) {
        request.setAttribute(Constant.REQUEST_HEADER_TOKEN_KEY, WebUtil.cookieGetSid(request));

        return userApiService.updatePwd(updatePwdRequest);
    }

    @PostMapping("/follow/{followed}")
    public ResultModel follow(@PathVariable("followed") Long followed, HttpServletRequest request) {
        request.setAttribute(Constant.REQUEST_HEADER_TOKEN_KEY, WebUtil.cookieGetSid(request));

        return userApiService.follow(followed);
    }

    @PostMapping("/cancel-follow/{followed}")
    public ResultModel cancelFollow(@PathVariable("followed") Long followed, HttpServletRequest request) {
        request.setAttribute(Constant.REQUEST_HEADER_TOKEN_KEY, WebUtil.cookieGetSid(request));

        return userApiService.cancelFollow(followed);
    }
}
