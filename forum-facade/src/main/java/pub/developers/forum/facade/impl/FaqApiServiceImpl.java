package pub.developers.forum.facade.impl;

import org.springframework.stereotype.Service;
import pub.developers.forum.api.model.PageRequestModel;
import pub.developers.forum.api.model.PageResponseModel;
import pub.developers.forum.api.model.ResultModel;
import pub.developers.forum.api.request.faq.*;
import pub.developers.forum.api.response.faq.FaqHotsResponse;
import pub.developers.forum.api.response.faq.FaqInfoResponse;
import pub.developers.forum.api.response.faq.FaqUserPageResponse;
import pub.developers.forum.api.service.FaqApiService;
import pub.developers.forum.app.manager.FaqManager;
import pub.developers.forum.app.support.IsLogin;
import pub.developers.forum.common.enums.UserRoleEn;
import pub.developers.forum.facade.support.ResultModelUtil;
import pub.developers.forum.facade.validator.FaqValidator;
import pub.developers.forum.facade.validator.PageRequestModelValidator;

import javax.annotation.Resource;
import java.util.List;

/**
 * FAQ (Frequently Asked Questions) API Service Implementation
 * Manages Q&A operations including creation, pagination, and solution marking
 * 
 * @author Qiangqiang.Bian
 * @create 2020/11/1
 **/
@Service
public class FaqApiServiceImpl implements FaqApiService {

    @Resource
    private FaqManager faqManager;

    /**
     * Save a new FAQ/question
     * 
     * @param request FAQ save request
     * @return ID of the created FAQ
     */
    @Override
    public ResultModel<Long> saveFaq(FaqSaveFaqRequest request) {
        FaqValidator.saveFaq(request);

        return ResultModelUtil.success(faqManager.saveFaq(request));
    }

    /**
     * Get paginated FAQ list for admin management
     * Requires admin role to access
     * 
     * @param pageRequestModel page request with admin filter
     * @return paginated FAQ list
     */
    @IsLogin(role = UserRoleEn.ADMIN)
    @Override
    public ResultModel<PageResponseModel<FaqUserPageResponse>> adminPage(PageRequestModel<FaqAdminPageRequest> pageRequestModel) {
        PageRequestModelValidator.validator(pageRequestModel);

        return ResultModelUtil.success(faqManager.adminPage(pageRequestModel));
    }

    /**
     * Get paginated FAQ list for regular users
     * 
     * @param pageRequestModel page request with user filter
     * @return paginated FAQ list
     */
    @Override
    public ResultModel<PageResponseModel<FaqUserPageResponse>> userPage(PageRequestModel<FaqUserPageRequest> pageRequestModel) {
        PageRequestModelValidator.validator(pageRequestModel);

        return ResultModelUtil.success(faqManager.userPage(pageRequestModel));
    }

    /**
     * Get paginated FAQ list created by a specific author
     * 
     * @param pageRequestModel page request with author filter
     * @return paginated FAQ list by author
     */
    @Override
    public ResultModel<PageResponseModel<FaqUserPageResponse>> authorPage(PageRequestModel<FaqAuthorPageRequest> pageRequestModel) {
        PageRequestModelValidator.validator(pageRequestModel);

        return ResultModelUtil.success(faqManager.authorPage(pageRequestModel));
    }

    /**
     * Get detailed information of a specific FAQ
     * 
     * @param id FAQ ID
     * @return FAQ detailed information
     */
    @Override
    public ResultModel<FaqInfoResponse> info(Long id) {
        return ResultModelUtil.success(faqManager.info(id));
    }

    /**
     * Get list of hot/popular FAQs
     * 
     * @param size number of hot FAQs to return
     * @return list of hot FAQs
     */
    @Override
    public ResultModel<List<FaqHotsResponse>> hots(int size) {
        return ResultModelUtil.success(faqManager.hots(size));
    }

    /**
     * Mark an answer as the solution to a FAQ/question
     * 
     * @param request solution marking request
     * @return success result
     */
    @Override
    public ResultModel solution(FaqSolutionRequest request) {
        FaqValidator.solution(request);

        faqManager.solution(request);

        return ResultModelUtil.success();
    }
}