package pub.developers.forum.domain.repository;

import pub.developers.forum.common.model.PageResult;
import pub.developers.forum.domain.entity.Comment;

import java.util.List;
import java.util.Set;

/**
 * Repository interface for managing comment operations
 * @author Qiangqiang.Bian
 * @create 2020/11/5
 * @desc
 **/
public interface CommentRepository {

    /**
     * Save a comment
     * @param comment the comment to save
     */
    void save(Comment comment);

    /**
     * Get a comment by ID
     * @param id the comment ID
     * @return the comment if found
     */
    Comment get(Long id);

    /**
     * Query comments by posts ID
     * @param postsId the posts ID
     * @return list of comments
     */
    List<Comment> queryByPostsId(Long postsId);

    /**
     * Query comments by reply IDs
     * @param replyIds the set of reply IDs
     * @return list of comments
     */
    List<Comment> queryInReplyIds(Set<Long> replyIds);

    /**
     * Query comments by page
     * @param pageNo the page number
     * @param pageSize the page size
     * @param postsId the posts ID
     * @return page result of comments
     */
    PageResult<Comment> page(Integer pageNo, Integer pageSize, Long postsId);

    /**
     * Delete comments by posts ID
     * @param postsId the posts ID
     */
    void deleteByPostsId(Long postsId);

    /**
     * Query comments by IDs
     * @param ids the set of comment IDs
     * @return list of comments
     */
    List<Comment> queryInIds(Set<Long> ids);
}