package pub.developers.forum.facade.impl;

import org.springframework.stereotype.Service;
import pub.developers.forum.api.model.PageRequestModel;
import pub.developers.forum.api.model.PageResponseModel;
import pub.developers.forum.api.model.ResultModel;
import pub.developers.forum.api.request.AdminBooleanRequest;
import pub.developers.forum.api.request.article.*;
import pub.developers.forum.api.response.article.ArticleInfoResponse;
import pub.developers.forum.api.response.article.ArticleQueryTypesResponse;
import pub.developers.forum.api.response.article.ArticleUserPageResponse;
import pub.developers.forum.api.service.ArticleApiService;
import pub.developers.forum.app.manager.ArticleManager;
import pub.developers.forum.facade.support.ResultModelUtil;
import pub.developers.forum.facade.validator.ArticleValidator;
import pub.developers.forum.facade.validator.PageRequestModelValidator;

import javax.annotation.Resource;
import java.util.List;

/**
 * Article API Service Implementation
 * Manages article operations including creation, categorization, pagination, and admin management
 * 
 * @author Qiangqiang.Bian
 * @create 2020/10/31
 **/
@Service
public class ArticleApiServiceImpl implements ArticleApiService {

    @Resource
    private ArticleManager articleManager;

    /**
     * Query all article types/categories
     * 
     * @return list of all article types
     */
    @Override
    public ResultModel<List<ArticleQueryTypesResponse>> queryAllTypes() {
        return ResultModelUtil.success(articleManager.queryAllTypes());
    }

    /**
     * Query article types available for admin management
     * 
     * @return list of admin-manageable article types
     */
    @Override
    public ResultModel<List<ArticleQueryTypesResponse>> queryAdminTypes() {
        return ResultModelUtil.success(articleManager.queryAdminTypes());
    }

    /**
     * Query article types available for editing articles
     * 
     * @return list of editable article types
     */
    @Override
    public ResultModel<List<ArticleQueryTypesResponse>> queryEditArticleTypes() {
        return ResultModelUtil.success(articleManager.queryEditArticleTypes());
    }

    /**
     * Add a new article type/category
     * 
     * @param request article type creation request
     * @return success result
     */
    @Override
    public ResultModel addType(ArticleAddTypeRequest request) {
        ArticleValidator.addType(request);

        articleManager.addType(request);

        return ResultModelUtil.success();
    }

    /**
     * Save a new article or update an existing one
     * 
     * @param request article save request
     * @return ID of the saved article
     */
    @Override
    public ResultModel<Long> saveArticle(ArticleSaveArticleRequest request) {
        ArticleValidator.saveArticle(request);

        return ResultModelUtil.success(articleManager.saveArticle(request));
    }

    /**
     * Get paginated article list for regular users
     * 
     * @param pageRequestModel page request with user filter
     * @return paginated article list
     */
    @Override
    public ResultModel<PageResponseModel<ArticleUserPageResponse>> userPage(PageRequestModel<ArticleUserPageRequest> pageRequestModel) {
        PageRequestModelValidator.validator(pageRequestModel);

        return ResultModelUtil.success(articleManager.userPage(pageRequestModel));
    }

    /**
     * Get paginated article list created by a specific author
     * 
     * @param pageRequestModel page request with author filter
     * @return paginated article list by author
     */
    @Override
    public ResultModel<PageResponseModel<ArticleUserPageResponse>> authorPage(PageRequestModel<ArticleAuthorPageRequest> pageRequestModel) {
        PageRequestModelValidator.validator(pageRequestModel);

        return ResultModelUtil.success(articleManager.authorPage(pageRequestModel));
    }

    /**
     * Get paginated article list for admin management
     * 
     * @param pageRequestModel page request with admin filter
     * @return paginated article list for admin
     */
    @Override
    public ResultModel<PageResponseModel<ArticleUserPageResponse>> adminPage(PageRequestModel<ArticleAdminPageRequest> pageRequestModel) {
        PageRequestModelValidator.validator(pageRequestModel);

        return ResultModelUtil.success(articleManager.adminPage(pageRequestModel));
    }

    /**
     * Get detailed information of a specific article
     * 
     * @param id article ID
     * @return article detailed information
     */
    @Override
    public ResultModel<ArticleInfoResponse> info(Long id) {

        return ResultModelUtil.success(articleManager.info(id));
    }

    /**
     * Set article as top/pinned or remove top status
     * 
     * @param booleanRequest boolean request with article ID and top status
     * @return success result
     */
    @Override
    public ResultModel adminTop(AdminBooleanRequest booleanRequest) {
        ArticleValidator.validatorBooleanRequest(booleanRequest);

        articleManager.adminTop(booleanRequest);

        return ResultModelUtil.success();
    }

    /**
     * Set article as official or remove official status
     * 
     * @param booleanRequest boolean request with article ID and official status
     * @return success result
     */
    @Override
    public ResultModel adminOfficial(AdminBooleanRequest booleanRequest) {
        ArticleValidator.validatorBooleanRequest(booleanRequest);

        articleManager.adminOfficial(booleanRequest);

        return ResultModelUtil.success();
    }

    /**
     * Set article as marrow/featured or remove marrow status
     * 
     * @param booleanRequest boolean request with article ID and marrow status
     * @return success result
     */
    @Override
    public ResultModel adminMarrow(AdminBooleanRequest booleanRequest) {
        ArticleValidator.validatorBooleanRequest(booleanRequest);

        articleManager.adminMarrow(booleanRequest);

        return ResultModelUtil.success();
    }

    /**
     * Get paginated list of article types for admin management
     * 
     * @param pageRequestModel page request with type filter
     * @return paginated article type list
     */
    @Override
    public ResultModel<PageResponseModel<ArticleQueryTypesResponse>> typePage(PageRequestModel<ArticleAdminTypePageRequest> pageRequestModel) {
        PageRequestModelValidator.validator(pageRequestModel);

        return ResultModelUtil.success(articleManager.typePage(pageRequestModel));
    }

    /**
     * Update article type audit state (approve/reject)
     * 
     * @param booleanRequest boolean request with type ID and audit state
     * @return success result
     */
    @Override
    public ResultModel typeAuditState(AdminBooleanRequest booleanRequest) {
        ArticleValidator.validatorBooleanRequest(booleanRequest);

        articleManager.typeAuditState(booleanRequest);

        return ResultModelUtil.success();
    }
}