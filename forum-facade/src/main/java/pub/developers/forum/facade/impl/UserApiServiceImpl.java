package pub.developers.forum.facade.impl;

import com.alibaba.fastjson.JSON;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pub.developers.forum.api.model.PageRequestModel;
import pub.developers.forum.api.model.PageResponseModel;
import pub.developers.forum.api.model.ResultModel;
import pub.developers.forum.api.request.AdminBooleanRequest;
import pub.developers.forum.api.request.user.*;
import pub.developers.forum.api.response.user.UserInfoResponse;
import pub.developers.forum.api.response.user.UserOptLogPageResponse;
import pub.developers.forum.api.response.user.UserPageResponse;
import pub.developers.forum.api.service.UserApiService;
import pub.developers.forum.app.manager.UserManager;
import pub.developers.forum.facade.support.ResultModelUtil;
import pub.developers.forum.facade.validator.ArticleValidator;
import pub.developers.forum.facade.validator.PageRequestModelValidator;
import pub.developers.forum.facade.validator.UserValidator;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;

/**
 * User API Service Implementation
 * Provides user-related operations including authentication, profile management, follow system, and admin functions
 * 
 * @author Qiangqiang.Bian
 * @create 20/7/30
 **/
@Service
public class UserApiServiceImpl implements UserApiService {

    @Resource
    private UserManager userManager;

    /**
     * Enable a user account
     * 
     * @param uid user ID
     * @return success result
     */
    @Override
    public ResultModel enable(Long uid) {
        userManager.enable(uid);

        return ResultModelUtil.success();
    }

    /**
     * Disable a user account
     * 
     * @param uid user ID
     * @return success result
     */
    @Override
    public ResultModel disable(Long uid) {
        userManager.disable(uid);

        return ResultModelUtil.success();
    }

    /**
     * Follow a user
     * 
     * @param followed user ID to follow
     * @return success result
     */
    @Override
    public ResultModel follow(Long followed) {
        userManager.follow(followed);

        return ResultModelUtil.success();
    }

    /**
     * Cancel following a user
     * 
     * @param followed user ID to unfollow
     * @return success result
     */
    @Override
    public ResultModel cancelFollow(Long followed) {
        userManager.cancelFollow(followed);

        return ResultModelUtil.success();
    }

    /**
     * Get paginated list of users that the specified user is following
     * 
     * @param pageRequestModel page request with user ID filter
     * @return paginated list of followed users
     */
    @Override
    public ResultModel<PageResponseModel<UserPageResponse>> pageFollower(PageRequestModel<Long> pageRequestModel) {
        PageRequestModelValidator.validator(pageRequestModel);
        pageRequestModel.setFilter(JSON.parseObject(JSON.toJSONString(pageRequestModel.getFilter()), Long.class));

        return ResultModelUtil.success(userManager.pageFollower(pageRequestModel));
    }

    /**
     * Get paginated list of fans (followers) of the specified user
     * 
     * @param pageRequestModel page request with user ID filter
     * @return paginated list of fans
     */
    @Override
    public ResultModel<PageResponseModel<UserPageResponse>> pageFans(PageRequestModel<Long> pageRequestModel) {
        PageRequestModelValidator.validator(pageRequestModel);
        pageRequestModel.setFilter(JSON.parseObject(JSON.toJSONString(pageRequestModel.getFilter()), Long.class));

        return ResultModelUtil.success(userManager.pageFans(pageRequestModel));
    }

    /**
     * Check if current user has followed the specified user
     * 
     * @param followed user ID to check
     * @return true if following, false otherwise
     */
    @Override
    public ResultModel<Boolean> hasFollow(Long followed) {
        return ResultModelUtil.success(userManager.hasFollow(followed));
    }

    /**
     * Get user information by authentication token
     * 
     * @param token authentication token
     * @return user information
     */
    @Override
    public ResultModel<UserInfoResponse> info(String token) {
        return ResultModelUtil.success(userManager.info(token));
    }

    /**
     * Get user information by user ID
     * 
     * @param uid user ID
     * @return user information
     */
    @Override
    public ResultModel<UserInfoResponse> info(Long uid) {
        return ResultModelUtil.success(userManager.info(uid));
    }

    /**
     * Register a new user account
     * 
     * @param request user registration request
     * @return authentication token
     */
    @Override
    public ResultModel<String> register(UserRegisterRequest request) {
        UserValidator.register(request);

        return ResultModelUtil.success(userManager.register(request));
    }

    /**
     * User login with email and password
     * 
     * @param request email login request
     * @return authentication token
     */
    @Override
    public ResultModel<String> emailLogin(UserEmailLoginRequest request) {
        UserValidator.emailLogin(request);

        return ResultModelUtil.success(userManager.emailRequestLogin(request));
    }

    /**
     * User logout
     * 
     * @param request logout request with token
     * @return success result
     */
    @Override
    public ResultModel logout(UserTokenLogoutRequest request) {
        UserValidator.logout(request);

        userManager.logout(request);

        return ResultModelUtil.success();
    }

    /**
     * Update user profile information
     * 
     * @param request user information update request
     * @return success result
     */
    @Override
    public ResultModel updateInfo(UserUpdateInfoRequest request) {
        UserValidator.updateInfo(request);

        userManager.updateInfo(request);

        return ResultModelUtil.success();
    }

    /**
     * Update user avatar/head image
     * 
     * @param linkFilenameData image link or filename data
     * @return success result
     */
    @Override
    public ResultModel updateHeadImg(String linkFilenameData) {
        userManager.updateHeadimg(linkFilenameData);
        return ResultModelUtil.success();
    }

    /**
     * Update user password
     * 
     * @param request password update request
     * @return success result
     */
    @Override
    public ResultModel updatePwd(UserUpdatePwdRequest request) {
        UserValidator.updatePwd(request);

        userManager.updatePwd(request);

        return ResultModelUtil.success();
    }

    /**
     * Get paginated user list for admin management
     * 
     * @param pageRequestModel page request with admin query filter
     * @return paginated user list
     */
    @Override
    public ResultModel<PageResponseModel<UserPageResponse>> adminPage(PageRequestModel<UserAdminPageRequest> pageRequestModel) {
        PageRequestModelValidator.validator(pageRequestModel);
        UserAdminPageRequest request = JSON.parseObject(JSON.toJSONString(pageRequestModel.getFilter()), UserAdminPageRequest.class);
        UserValidator.adminPage(request);
        pageRequestModel.setFilter(request);

        return ResultModelUtil.success(userManager.page(pageRequestModel));
    }

    /**
     * Get paginated list of active users
     * 
     * @param pageRequestModel page request
     * @return paginated active user list
     */
    @Override
    public ResultModel<PageResponseModel<UserPageResponse>> pageActive(PageRequestModel pageRequestModel) {
        return ResultModelUtil.success(userManager.pageActive(pageRequestModel));
    }

    /**
     * Get paginated user operation logs
     * 
     * @param pageRequestModel page request with operation log filter
     * @return paginated operation log list
     */
    @Override
    public ResultModel<PageResponseModel<UserOptLogPageResponse>> pageOptLog(PageRequestModel<UserOptLogPageRequest> pageRequestModel) {
        PageRequestModelValidator.validator(pageRequestModel);
        UserOptLogPageRequest request = JSON.parseObject(JSON.toJSONString(pageRequestModel.getFilter()), UserOptLogPageRequest.class);
        pageRequestModel.setFilter(request);

        return ResultModelUtil.success(userManager.pageOptLog(pageRequestModel));
    }

    /**
     * Update user role (admin privileges)
     * 
     * @param booleanRequest boolean request with user ID and role status
     * @return success result
     */
    @Override
    public ResultModel updateRole(AdminBooleanRequest booleanRequest) {
        ArticleValidator.validatorBooleanRequest(booleanRequest);

        userManager.updateRole(booleanRequest);

        return ResultModelUtil.success();
    }
}