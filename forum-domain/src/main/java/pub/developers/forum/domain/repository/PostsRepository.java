package pub.developers.forum.domain.repository;

import pub.developers.forum.domain.entity.BasePosts;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Repository interface for managing posts operations
 * @author Qiangqiang.Bian
 * @create 2020/11/6
 * @desc
 **/
public interface PostsRepository {

    /**
     * Get a post by ID
     * @param id the post ID
     * @return the post if found
     */
    BasePosts get(Long id);

    /**
     * Query posts by IDs
     * @param postsIds the set of post IDs
     * @return list of posts
     */
    List<BasePosts> queryInIds(Set<Long> postsIds);

    /**
     * Get all post IDs by author ID
     * @param authorId the author ID
     * @return list of post IDs
     */
    List<Long> getAllIdByAuthorId(Long authorId);

    /**
     * Increase comment count for a post
     * @param id the post ID
     * @param updateAt the update timestamp
     */
    void increaseComments(Long id, Date updateAt);

    /**
     * Decrease comment count for a post
     * @param id the post ID
     * @param updateAt the update timestamp
     */
    void decreaseComments(Long id, Date updateAt);

    /**
     * Increase view count for a post
     * @param id the post ID
     * @param updateAt the update timestamp
     */
    void increaseViews(Long id, Date updateAt);

    /**
     * Increase approval count for a post
     * @param id the post ID
     * @param updateAt the update timestamp
     */
    void increaseApproval(Long id, Date updateAt);

    /**
     * Decrease approval count for a post
     * @param id the post ID
     * @param updateAt the update timestamp
     */
    void decreaseApproval(Long id, Date updateAt);

    /**
     * Delete a post by ID
     * @param id the post ID
     */
    void delete(Long id);

    /**
     * Update a post
     * @param basePosts the post to update
     */
    void update(BasePosts basePosts);
}