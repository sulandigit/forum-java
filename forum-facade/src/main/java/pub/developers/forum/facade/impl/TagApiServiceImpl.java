package pub.developers.forum.facade.impl;

import org.springframework.stereotype.Service;
import pub.developers.forum.api.model.PageRequestModel;
import pub.developers.forum.api.model.PageResponseModel;
import pub.developers.forum.api.model.ResultModel;
import pub.developers.forum.api.request.AdminBooleanRequest;
import pub.developers.forum.api.request.tag.TagCreateRequest;
import pub.developers.forum.api.request.tag.TagPageRequest;
import pub.developers.forum.api.response.tag.TagPageResponse;
import pub.developers.forum.api.response.tag.TagQueryResponse;
import pub.developers.forum.api.service.TagApiService;
import pub.developers.forum.api.vo.PostsVO;
import pub.developers.forum.app.manager.TagManager;
import pub.developers.forum.common.support.CheckUtil;
import pub.developers.forum.facade.support.ResultModelUtil;
import pub.developers.forum.facade.validator.ArticleValidator;
import pub.developers.forum.facade.validator.PageRequestModelValidator;
import pub.developers.forum.facade.validator.TagValidator;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;

/**
 * Tag API Service Implementation
 * Handles tag operations including creation, querying, pagination, and audit management
 * 
 * @author Qiangqiang.Bian
 * @create 20/7/30
 **/
@Service
public class TagApiServiceImpl implements TagApiService {

    @Resource
    private TagManager tagManager;

    /**
     * Query all referenced tags
     * 
     * @return list of referenced tags
     */
    @Override
    public ResultModel<List<TagQueryResponse>> queryAllRef() {
        return ResultModelUtil.success(tagManager.queryAllRef());
    }

    /**
     * Create a new tag
     * 
     * @param request tag creation request
     * @return success result
     */
    @Override
    public ResultModel create(TagCreateRequest request) {
        TagValidator.create(request);

        tagManager.create(request);

        return ResultModelUtil.success();
    }

    /**
     * Get tag information by tag name
     * 
     * @param name tag name
     * @return tag information
     */
    @Override
    public ResultModel<TagQueryResponse> getByName(String name) {
        return ResultModelUtil.success(tagManager.getByName(name));
    }

    /**
     * Query all tags in the system
     * 
     * @return list of all tags
     */
    @Override
    public ResultModel<List<TagQueryResponse>> queryAll() {
        return ResultModelUtil.success(tagManager.queryAll());
    }

    /**
     * Query tags by a set of tag IDs
     * 
     * @param ids set of tag IDs
     * @return list of matching tags
     */
    @Override
    public ResultModel<List<TagQueryResponse>> queryInIds(Set<Long> ids) {
        CheckUtil.checkParamToast(ids, "ids");

        return ResultModelUtil.success(tagManager.queryInIds(ids));
    }

    /**
     * Get paginated posts associated with a specific tag
     * 
     * @param pageRequestModel page request with tag ID filter
     * @return paginated list of posts
     */
    @Override
    public ResultModel<PageResponseModel<PostsVO>> pagePosts(PageRequestModel<Long> pageRequestModel) {
        PageRequestModelValidator.validator(pageRequestModel);

        return ResultModelUtil.success(tagManager.pagePosts(pageRequestModel));
    }

    /**
     * Get paginated posts associated with multiple tags
     * 
     * @param pageRequestModel page request with tag IDs filter
     * @return paginated list of posts
     */
    @Override
    public ResultModel<PageResponseModel<PostsVO>> pagePostsByTagIds(PageRequestModel<Set<Long>> pageRequestModel) {
        PageRequestModelValidator.validator(pageRequestModel);

        return ResultModelUtil.success(tagManager.pagePostsByTagIds(pageRequestModel));
    }

    /**
     * Get paginated list of tags for admin management
     * 
     * @param pageRequestModel page request with tag filter
     * @return paginated tag list
     */
    @Override
    public ResultModel<PageResponseModel<TagPageResponse>> page(PageRequestModel<TagPageRequest> pageRequestModel) {
        PageRequestModelValidator.validator(pageRequestModel);

        return ResultModelUtil.success(tagManager.page(pageRequestModel));
    }

    /**
     * Update tag audit state (approve/reject)
     * 
     * @param booleanRequest boolean request with tag ID and audit state
     * @return success result
     */
    @Override
    public ResultModel auditState(AdminBooleanRequest booleanRequest) {
        ArticleValidator.validatorBooleanRequest(booleanRequest);

        tagManager.tagAuditState(booleanRequest);

        return ResultModelUtil.success();
    }
}