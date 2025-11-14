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
 * @author Qiangqiang.Bian
 * @create 2020/12/2
 * @desc
 **/
public abstract class AbstractPostsRepository {

    @Resource
    PostsDAO postsDAO;

    @Resource
    UserDAO userDAO;

    @Resource
    TagPostsMappingDAO tagPostsMappingDAO;

    @Resource
    TagDAO tagDAO;

    public PageResult<Posts> basePagePosts(List<Long> postsIds, PageInfo pageInfo, AuditStateEn auditStateEn) {
        // 1. 查询帖子数据
        List<PostsDO> queryPostsDOS = queryPostsByIdsAndState(postsIds, auditStateEn);
        if (ObjectUtils.isEmpty(queryPostsDOS)) {
            return buildEmptyPageResult(pageInfo);
        }

        // 2. 按原始 postsIds 顺序排序（使用 Map 优化性能，避免 O(n²) 复杂度）
        List<PostsDO> sortedPostsDOS = sortPostsByOriginalOrder(postsIds, queryPostsDOS);

        // 3. 查询用户信息
        List<User> users = queryUsersByPostsAuthor(sortedPostsDOS);

        // 4. 查询标签映射关系
        List<TagPostsMappingDO> tagPostsMappingDOList = tagPostsMappingDAO.queryInPostsIds(new HashSet<>(postsIds));

        // 5. 查询标签详情
        List<Tag> tags = queryTagsByMapping(tagPostsMappingDOList);

        // 6. 组装结果
        List<Posts> postsList = PostsTransfer.toPostsList(sortedPostsDOS, users, tagPostsMappingDOList, tags);
        return PageResult.build(pageInfo.getTotal(), pageInfo.getSize(), postsList);
    }

    /**
     * 根据 ID 和审核状态查询帖子
     */
    private List<PostsDO> queryPostsByIdsAndState(List<Long> postsIds, AuditStateEn auditStateEn) {
        if (ObjectUtils.isEmpty(auditStateEn)) {
            return postsDAO.queryInIds(new HashSet<>(postsIds));
        }
        return postsDAO.queryInIdsAndState(new HashSet<>(postsIds), auditStateEn.getValue());
    }

    /**
     * 按原始顺序排序帖子（使用 Map 提升性能）
     */
    private List<PostsDO> sortPostsByOriginalOrder(List<Long> postsIds, List<PostsDO> queryPostsDOS) {
        // 使用 Map 提升查找效率，从 O(n²) 优化到 O(n)
        Map<Long, PostsDO> postsMap = queryPostsDOS.stream()
                .collect(Collectors.toMap(PostsDO::getId, postsDO -> postsDO, (old, now) -> now));

        return postsIds.stream()
                .map(postsMap::get)
                .filter(postsDO -> postsDO != null)
                .collect(Collectors.toList());
    }

    /**
     * 根据帖子作者查询用户信息
     */
    private List<User> queryUsersByPostsAuthor(List<PostsDO> postsDOS) {
        Set<Long> userIds = SafesUtil.ofList(postsDOS).stream()
                .map(PostsDO::getAuthorId)
                .collect(Collectors.toSet());
        return UserTransfer.toUsers(userDAO.queryInIds(userIds));
    }

    /**
     * 根据标签映射关系查询标签详情
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
     * 构建空的分页结果
     */
    private PageResult<Posts> buildEmptyPageResult(PageInfo pageInfo) {
        return PageResult.build(pageInfo.getTotal(), pageInfo.getSize(), Collections.emptyList());
    }
}
