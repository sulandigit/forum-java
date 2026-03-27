package pub.developers.forum.facade.impl;

import org.springframework.stereotype.Service;
import pub.developers.forum.api.model.ResultModel;
import pub.developers.forum.api.request.github.GithubOauthLoginRequest;
import pub.developers.forum.api.service.GithubApiService;
import pub.developers.forum.app.manager.GithubManager;
import pub.developers.forum.common.support.CheckUtil;
import pub.developers.forum.facade.support.ResultModelUtil;

import javax.annotation.Resource;

/**
 * GitHub API Service Implementation
 * Handles GitHub OAuth authentication and integration
 * 
 * @author Qiangqiang.Bian
 * @create 2021/5/15
 **/
@Service
public class GithubApiServiceImpl implements GithubApiService {

    @Resource
    private GithubManager githubManager;

    /**
     * Handle GitHub OAuth login
     * Exchanges OAuth code for user authentication token
     * 
     * @param request GitHub OAuth login request containing authorization code
     * @return authentication token
     */
    @Override
    public ResultModel<String> oauthLogin(GithubOauthLoginRequest request) {
        CheckUtil.checkParamToast(request, "request");
        CheckUtil.checkParamToast(request.getCode(), "code");

        return ResultModelUtil.success(githubManager.oauthLogin(request));
    }
}