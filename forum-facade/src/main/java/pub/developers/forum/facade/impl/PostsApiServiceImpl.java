package pub.developers.forum.facade.impl;

import org.springframework.stereotype.Service;
import pub.developers.forum.api.model.PageRequestModel;
import pub.developers.forum.api.model.PageResponseModel;
import pub.developers.forum.api.model.ResultModel;
import pub.developers.forum.api.request.AdminBooleanRequest;
import pub.developers.forum.api.service.PostsApiService;
import pub.developers.forum.api.vo.PostsVO;
import pub.developers.forum.app.manager.PostsManager;
import pub.developers.forum.facade.support.ResultModelUtil;
import pub.developers.forum.facade.validator.ArticleValidator;

import javax.annotation.Resource;

/**
 * Posts API Service Implementation
 * Manages post operations including deletion, pagination, and audit state management
 * 
 * @author Qiangqiang.Bian
 * @create 2020/11/25
 **/
@Service
public class PostsApiServiceImpl implements PostsApiService {

    @Resource
    private PostsManager postsManager;

    /**
     * Delete a post by ID
     * 
     * @param id post ID to delete
     * @return success result
     */
    @Override
    public ResultModel delete(Long id) {
        postsManager.delete(id);

        return ResultModelUtil.success();
    }

    /**
     * Get paginated list of featured/recommended posts
     * 
     * @param pageRequestModel page request
     * @return paginated list of featured posts
     */
    @Override
    public ResultModel<PageResponseModel<PostsVO>> pagePostsFood(PageRequestModel pageRequestModel) {
        return ResultModelUtil.success(postsManager.pagePostsFood(pageRequestModel));
    }

    /**
     * Update post audit state (approve/reject)
     * 
     * @param booleanRequest boolean request with post ID and audit state
     * @return success result
     */
    @Override
    public ResultModel auditState(AdminBooleanRequest booleanRequest) {
        ArticleValidator.validatorBooleanRequest(booleanRequest);

        postsManager.auditState(booleanRequest);

        return ResultModelUtil.success();
    }

}