package pub.developers.forum.facade.impl;

import org.springframework.stereotype.Service;
import pub.developers.forum.api.model.PageRequestModel;
import pub.developers.forum.api.model.PageResponseModel;
import pub.developers.forum.api.model.ResultModel;
import pub.developers.forum.api.request.comment.CommentCreateRequest;
import pub.developers.forum.api.response.comment.CommentPageResponse;
import pub.developers.forum.api.service.CommentApiService;
import pub.developers.forum.app.manager.CommentManager;
import pub.developers.forum.facade.support.ResultModelUtil;
import pub.developers.forum.facade.validator.CommentValidator;
import pub.developers.forum.facade.validator.PageRequestModelValidator;

import javax.annotation.Resource;

/**
 * @author Qiangqiang.Bian
 * @create 2020/11/6
 * @desc
 **/
@Service
public class CommentApiServiceImpl implements CommentApiService {

    @Resource
    private CommentManager commentManager;

    @Override
    public ResultModel create(CommentCreateRequest request) {
        CommentValidator.create(request);

        Long commentId = commentManager.create(request);

        return ResultModelUtil.success(commentId);
    }

    @Override
    public ResultModel<PageResponseModel<CommentPageResponse>> page(PageRequestModel<Long> pageRequest) {
        PageRequestModelValidator.validator(pageRequest);

        PageResponseModel<CommentPageResponse> pageResponse = commentManager.page(pageRequest);

        return ResultModelUtil.success(pageResponse);
    }
}