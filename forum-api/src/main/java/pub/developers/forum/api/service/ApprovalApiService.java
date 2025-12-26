package pub.developers.forum.api.service;

import pub.developers.forum.api.model.ResultModel;

/**
 * Approval API Service interface for managing post approvals
 * 
 * @author Qiangqiang.Bian
 * @create 2020/12/1
 * @desc
 **/
public interface ApprovalApiService {

    /**
     * Create an approval for a post
     * 
     * @param postsId the ID of the post to approve
     * @return ResultModel containing the approval ID
     */
    ResultModel<Long> create(Long postsId);

    /**
     * Delete an approval for a post
     * 
     * @param postsId the ID of the post to remove approval from
     * @return ResultModel containing the deleted approval ID
     */
    ResultModel<Long> delete(Long postsId);

    /**
     * Check if a post has been approved
     * 
     * @param postsId the ID of the post to check
     * @return ResultModel containing true if the post has approval, false otherwise
     */
    ResultModel<Boolean> hasApproval(Long postsId);

}