package pub.developers.forum.domain.repository;

import pub.developers.forum.common.model.PageRequest;
import pub.developers.forum.common.model.PageResult;
import pub.developers.forum.domain.entity.Follow;
import pub.developers.forum.domain.entity.User;

import java.util.List;

/**
 * Repository interface for managing user operations
 * @author Qiangqiang.Bian
 * @create 2020/7/31
 * @desc
 **/
public interface UserRepository {

    /**
     * Save a user
     * @param user the user to save
     */
    void save(User user);

    /**
     * Get a user by ID
     * @param id the user ID
     * @return the user if found
     */
    User get(Long id);

    /**
     * Get a user by email
     * @param email the user email
     * @return the user if found
     */
    User getByEmail(String email);

    /**
     * Update a user
     * @param user the user to update
     */
    void update(User user);

    /**
     * Query users by IDs
     * @param ids the list of user IDs
     * @return list of users
     */
    List<User> queryByIds(List<Long> ids);

    /**
     * Query users by page
     * @param pageRequest the page request
     * @return page result of users
     */
    PageResult<User> page(PageRequest<User> pageRequest);

    /**
     * Follow a user
     * @param followed the followed user ID
     * @param id the follower user ID
     */
    void follow(Long followed, Long id);

    /**
     * Query followers by page
     * @param longPageRequest the page request with user ID
     * @return page result of follower users
     */
    PageResult<User> pageFollower(PageRequest<Long> longPageRequest);

    /**
     * Query fans by page
     * @param longPageRequest the page request with user ID
     * @return page result of fan users
     */
    PageResult<User> pageFans(PageRequest<Long> longPageRequest);

    /**
     * Get follow relationship
     * @param followed the followed user ID
     * @param follower the follower user ID
     * @return the follow relationship if exists
     */
    Follow getFollow(Long followed, Long follower);

    /**
     * Cancel a follow relationship
     * @param followId the follow ID
     */
    void cancelFollow(Long followId);

    /**
     * Query active users by page
     * @param pageRequest the page request
     * @return page result of active users
     */
    PageResult<User> pageActive(PageRequest pageRequest);
}