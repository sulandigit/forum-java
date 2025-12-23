package pub.developers.forum.facade.impl;

import org.springframework.stereotype.Service;
import pub.developers.forum.api.model.PageRequestModel;
import pub.developers.forum.api.model.PageResponseModel;
import pub.developers.forum.api.model.ResultModel;
import pub.developers.forum.api.service.SearchApiService;
import pub.developers.forum.api.vo.PostsVO;
import pub.developers.forum.app.manager.SearchManager;
import pub.developers.forum.facade.support.ResultModelUtil;
import pub.developers.forum.facade.validator.PageRequestModelValidator;

import javax.annotation.Resource;

/**
 * Search API Service Implementation
 * Provides search functionality for posts and content
 * 
 * @author Qiangqiang.Bian
 * @create 2020/12/2
 **/
@Service
public class SearchApiServiceImpl implements SearchApiService {

    @Resource
    private SearchManager searchManager;

    /**
     * Search posts by keyword with pagination
     * 
     * @param pageRequestModel page request with search keyword filter
     * @return paginated list of matching posts
     */
    @Override
    public ResultModel<PageResponseModel<PostsVO>> pagePostsSearch(PageRequestModel<String> pageRequestModel) {
        PageRequestModelValidator.validator(pageRequestModel);

        return ResultModelUtil.success(searchManager.pagePostsSearch(pageRequestModel));
    }

}