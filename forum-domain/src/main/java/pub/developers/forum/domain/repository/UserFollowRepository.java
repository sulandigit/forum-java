package pub.developers.forum.domain.repository;

import pub.developers.forum.common.enums.FollowedTypeEn;

import java.util.List;

/**
 * Repository interface for managing user follow operations
 * @author Qiangqiang.Bian
 * @create 2020/12/3
 * @desc
 **/
public interface UserFollowRepository {

    /**
     * Get all follower IDs for a user by type
     * @param follower the follower user ID
     * @param type the followed type
     * @return list of follower IDs
     */
    List<Long> getAllFollowerIds(Long follower, FollowedTypeEn type);
}