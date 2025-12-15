package pub.developers.forum.domain.repository;

import pub.developers.forum.common.model.PageResult;
import pub.developers.forum.domain.entity.Comment;

import java.util.List;
import java.util.Set;

/**
 * Repository interface for Comment entity operations
 * 
 * @author Qiangqiang.Bian
 * @create 2020/11/5
 * @desc
 **/
public interface CommentRepository {

    /**
     * Save a new comment to the repository
     * 
     * @param comment the comment to save
     */
    void save(Comment comment);

    /**
     * Retrieve a comment by its ID
     * 
     * @param id the comment ID
     * @return the comment entity
     */
    Comment get(Long id);

    /**
     * Query comments by posts ID
     * 
     * @param postsId the posts ID
     * @return list of comments for the specified posts
     */
    List<Comment> queryByPostsId(Long postsId);

    /**
     * Query comments by reply IDs
     * 
     * @param replyIds set of reply IDs
     * @return list of comments matching the reply IDs
     */
    List<Comment> queryInReplyIds(Set<Long> replyIds);

    /**
     * Retrieve a paginated list of comments for a specific posts
     * 
     * @param pageNo the page number
     * @param pageSize the page size
     * @param postsId the posts ID
     * @return paginated result containing comments
     */
    PageResult<Comment> page(Integer pageNo, Integer pageSize, Long postsId);

    /**
     * Delete all comments associated with a posts
     * 
     * @param postsId the posts ID
     */
    void deleteByPostsId(Long postsId);

    /**
     * Query comments by IDs
     * 
     * @param ids set of comment IDs
     * @return list of comments matching the IDs
     */
    List<Comment> queryInIds(Set<Long> ids);
}