package pub.developers.forum.infrastructure;

import com.github.pagehelper.PageInfo;
import org.springframework.util.ObjectUtils;
import pub.developers.forum.common.enums.AuditStateEn;
import pub.developers.forum.common.model.PageResult;
import pub.developers.forum.common.support.SafesUtil;
import pub.developers.forum.domain.entity.Posts;
import pub.developers.forum.domain.entity.Tag;
import pub.developers.forum.domain.entity.User;
import pub.developers.forum.infrastructure.dal.dao.PostsDAO;
import pub.developers.forum.infrastructure.dal.dao.TagDAO;
import pub.developers.forum.infrastructure.dal.dao.TagPostsMappingDAO;
import pub.developers.forum.infrastructure.dal.dao.UserDAO;
import pub.developers.forum.infrastructure.dal.dataobject.PostsDO;
import pub.developers.forum.infrastructure.dal.dataobject.TagPostsMappingDO;
import pub.developers.forum.infrastructure.transfer.PostsTransfer;
import pub.developers.forum.infrastructure.transfer.TagTransfer;
import pub.developers.forum.infrastructure.transfer.UserTransfer;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Abstract repository for posts operations.
 * Provides common query methods for posts with pagination support.
 *
 * @author Qiangqiang.Bian
 * @create 2020/12/2
 */
public abstract class AbstractPostsRepository {

    @Resource
    PostsDAO postsDAO;

    @Resource
    UserDAO userDAO;

    @Resource
    TagPostsMappingDAO tagPostsMappingDAO;

    @Resource
    TagDAO tagDAO;

    /**
     * Query posts by IDs with pagination and optional audit state filter.
     * Results are sorted according to the original order of postsIds.
     *
     * @param postsIds list of post IDs to query
     * @param pageInfo pagination information
     * @param auditStateEn optional audit state filter
     * @return paginated result containing posts with associated users and tags
     */
    public PageResult<Posts> basePagePosts(List<Long> postsIds, PageInfo pageInfo, AuditStateEn auditStateEn) {
        // Query posts data
        List<PostsDO> queryPostsDOS = queryPostsByIdsAndState(postsIds, auditStateEn);
        if (ObjectUtils.isEmpty(queryPostsDOS)) {
            return buildEmptyPageResult(pageInfo);
        }

        // Sort posts by original postsIds order (using Map to optimize performance, avoid O(n²) complexity)
        List<PostsDO> sortedPostsDOS = sortPostsByOriginalOrder(postsIds, queryPostsDOS);

        // Query user information
        List<User> users = queryUsersByPostsAuthor(sortedPostsDOS);

        // Query tag-post mapping relationships
        List<TagPostsMappingDO> tagPostsMappingDOList = tagPostsMappingDAO.queryInPostsIds(new HashSet<>(postsIds));

        // Query tag details
        List<Tag> tags = queryTagsByMapping(tagPostsMappingDOList);

        // Assemble results
        List<Posts> postsList = PostsTransfer.toPostsList(sortedPostsDOS, users, tagPostsMappingDOList, tags);
        return PageResult.build(pageInfo.getTotal(), pageInfo.getSize(), postsList);
    }

    /**
     * Query posts by IDs and optional audit state.
     *
     * @param postsIds list of post IDs
     * @param auditStateEn optional audit state filter
     * @return list of post data objects
     */
    private List<PostsDO> queryPostsByIdsAndState(List<Long> postsIds, AuditStateEn auditStateEn) {
        if (ObjectUtils.isEmpty(auditStateEn)) {
            return postsDAO.queryInIds(new HashSet<>(postsIds));
        }
        return postsDAO.queryInIdsAndState(new HashSet<>(postsIds), auditStateEn.getValue());
    }

    /**
     * Sort posts by the original order of post IDs.
     * Uses HashMap to improve performance from O(n²) to O(n).
     *
     * @param postsIds original order of post IDs
     * @param queryPostsDOS queried post data objects
     * @return posts sorted by original order
     */
    private List<PostsDO> sortPostsByOriginalOrder(List<Long> postsIds, List<PostsDO> queryPostsDOS) {
        // Use Map to improve lookup efficiency from O(n²) to O(n)
        Map<Long, PostsDO> postsMap = queryPostsDOS.stream()
                .collect(Collectors.toMap(PostsDO::getId, postsDO -> postsDO, (old, now) -> now));

        return postsIds.stream()
                .map(postsMap::get)
                .filter(postsDO -> postsDO != null)
                .collect(Collectors.toList());
    }

    /**
     * Query user information by post authors.
     *
     * @param postsDOS list of posts
     * @return list of users who authored the posts
     */
    private List<User> queryUsersByPostsAuthor(List<PostsDO> postsDOS) {
        Set<Long> userIds = SafesUtil.ofList(postsDOS).stream()
                .map(PostsDO::getAuthorId)
                .collect(Collectors.toSet());
        return UserTransfer.toUsers(userDAO.queryInIds(userIds));
    }

    /**
     * Query tag details by tag-post mapping relationships.
     *
     * @param tagPostsMappingDOList list of tag-post mappings
     * @return list of tags
     */
    private List<Tag> queryTagsByMapping(List<TagPostsMappingDO> tagPostsMappingDOList) {
        if (ObjectUtils.isEmpty(tagPostsMappingDOList)) {
            return Collections.emptyList();
        }

        Set<Long> tagIds = SafesUtil.ofList(tagPostsMappingDOList).stream()
                .map(TagPostsMappingDO::getTagId)
                .collect(Collectors.toSet());
        return TagTransfer.toTags(tagDAO.queryInIds(tagIds));
    }

    /**
     * Build an empty page result.
     *
     * @param pageInfo pagination information
     * @return empty page result
     */
    private PageResult<Posts> buildEmptyPageResult(PageInfo pageInfo) {
        return PageResult.build(pageInfo.getTotal(), pageInfo.getSize(), Collections.emptyList());
    }
}
