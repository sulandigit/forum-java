package pub.developers.forum.domain.repository;

import pub.developers.forum.domain.entity.Approval;

/**
 * Repository interface for managing approval operations
 * @author Qiangqiang.Bian
 * @create 2020/12/1
 * @desc
 **/
public interface ApprovalRepository {

    /**
     * Save an approval
     * @param approval the approval to save
     */
    void save(Approval approval);

    /**
     * Delete an approval by ID
     * @param approvalId the approval ID to delete
     */
    void delete(Long approvalId);

    /**
     * Get an approval by posts ID and user ID
     * @param postsId the posts ID
     * @param userId the user ID
     * @return the approval if found
     */
    Approval get(Long postsId, Long userId);

}