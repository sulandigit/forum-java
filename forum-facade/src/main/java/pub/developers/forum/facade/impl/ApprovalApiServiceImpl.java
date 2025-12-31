package pub.developers.forum.facade.impl;

import org.springframework.stereotype.Service;
import pub.developers.forum.api.model.ResultModel;
import pub.developers.forum.api.service.ApprovalApiService;
import pub.developers.forum.app.manager.ApprovalManager;
import pub.developers.forum.facade.support.ResultModelUtil;

import javax.annotation.Resource;

/**
 * Implementation of ApprovalApiService
 * 
 * @author Qiangqiang.Bian
 * @create 2020/12/1
 * @desc Approval API service implementation for managing post approvals
 **/
@Service
public class ApprovalApiServiceImpl implements ApprovalApiService {

    @Resource
    private ApprovalManager approvalManager;

    /**
     * Create an approval for a post
     * 
     * @param postsId the ID of the post to approve
     * @return result model containing the approval ID
     */
    @Override
    public ResultModel<Long> create(Long postsId) {
        // Delegate to approval manager to create approval
        return ResultModelUtil.success(approvalManager.create(postsId));
    }

    /**
     * Delete an approval for a post
     * 
     * @param postsId the ID of the post whose approval should be deleted
     * @return result model containing the deleted approval ID
     */
    @Override
    public ResultModel<Long> delete(Long postsId) {
        // Delegate to approval manager to delete approval
        return ResultModelUtil.success(approvalManager.delete(postsId));
    }

    /**
     * Check if a post has been approved
     * 
     * @param postsId the ID of the post to check
     * @return result model containing true if post is approved, false otherwise
     */
    @Override
    public ResultModel<Boolean> hasApproval(Long postsId) {
        // Delegate to approval manager to check approval status
        return ResultModelUtil.success(approvalManager.hasApproval(postsId));
    }

}