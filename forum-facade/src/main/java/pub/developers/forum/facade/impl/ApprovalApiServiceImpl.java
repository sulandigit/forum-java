package pub.developers.forum.facade.impl;

import org.springframework.stereotype.Service;
import pub.developers.forum.api.model.ResultModel;
import pub.developers.forum.api.service.ApprovalApiService;
import pub.developers.forum.app.manager.ApprovalManager;
import pub.developers.forum.facade.support.ResultModelUtil;

import javax.annotation.Resource;

/**
 * Approval API Service Implementation
 * Manages user approval/like operations for posts
 * 
 * @author Qiangqiang.Bian
 * @create 2020/12/1
 **/
@Service
public class ApprovalApiServiceImpl implements ApprovalApiService {

    @Resource
    private ApprovalManager approvalManager;

    /**
     * Create an approval/like for a post
     * 
     * @param postsId post ID to approve/like
     * @return approval count after creation
     */
    @Override
    public ResultModel<Long> create(Long postsId) {
        return ResultModelUtil.success(approvalManager.create(postsId));
    }

    /**
     * Delete an approval/like from a post
     * 
     * @param postsId post ID to remove approval/like from
     * @return approval count after deletion
     */
    @Override
    public ResultModel<Long> delete(Long postsId) {
        return ResultModelUtil.success(approvalManager.delete(postsId));
    }

    /**
     * Check if current user has approved/liked a specific post
     * 
     * @param postsId post ID to check
     * @return true if user has approved/liked, false otherwise
     */
    @Override
    public ResultModel<Boolean> hasApproval(Long postsId) {
        return ResultModelUtil.success(approvalManager.hasApproval(postsId));
    }

}