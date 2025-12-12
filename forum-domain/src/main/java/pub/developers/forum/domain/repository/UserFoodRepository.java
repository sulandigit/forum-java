package pub.developers.forum.domain.repository;

import pub.developers.forum.common.model.PageRequest;
import pub.developers.forum.common.model.PageResult;
import pub.developers.forum.domain.entity.Posts;
import pub.developers.forum.domain.entity.UserFood;

import java.util.List;

/**
 * Repository interface for managing user food (favorites) operations
 * @author Qiangqiang.Bian
 * @create 2020/12/3
 * @desc
 **/
public interface UserFoodRepository {

    /**
     * Batch save user foods
     * @param userFoods the list of user foods to save
     */
    void batchSave(List<UserFood> userFoods);

    /**
     * Query posts by page for a user
     * @param pageRequest the page request with user ID
     * @return page result of posts
     */
    PageResult<Posts> pagePosts(PageRequest<Long> pageRequest);

    /**
     * Delete user food by posts ID
     * @param postsId the posts ID
     */
    void deleteByPostsId(Long postsId);
}