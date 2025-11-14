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
        List<PostsDO> queryPostsDOS = queryPostsByIdsAndState(postsIds, auditStateEn);
        if (ObjectUtils.isEmpty(queryPostsDOS)) {
            return buildEmptyPageResult(pageInfo);
        }

        List<PostsDO> sortedPostsDOS = sortPostsByOriginalOrder(postsIds, queryPostsDOS);

        List<User> users = queryUsersByPostsAuthor(sortedPostsDOS);

        List<TagPostsMappingDO> tagPostsMappingDOList = tagPostsMappingDAO.queryInPostsIds(new HashSet<>(postsIds));

        List<Tag> tags = queryTagsByMapping(tagPostsMappingDOList);
        List<Posts> postsList = PostsTransfer.toPostsList(sortedPostsDOS, users, tagPostsMappingDOList, tags);
        return PageResult.build(pageInfo.getTotal(), pageInfo.getSize(), postsList);
    }

    private List<PostsDO> queryPostsByIdsAndState(List<Long> postsIds, AuditStateEn auditStateEn) {
        if (ObjectUtils.isEmpty(auditStateEn)) {
            return postsDAO.queryInIds(new HashSet<>(postsIds));
        }
        return postsDAO.queryInIdsAndState(new HashSet<>(postsIds), auditStateEn.getValue());
    }

    private List<PostsDO> sortPostsByOriginalOrder(List<Long> postsIds, List<PostsDO> queryPostsDOS) {
        Map<Long, PostsDO> postsMap = queryPostsDOS.stream()
                .collect(Collectors.toMap(PostsDO::getId, postsDO -> postsDO, (old, now) -> now));

        return postsIds.stream()
                .map(postsMap::get)
                .filter(postsDO -> postsDO != null)
                .collect(Collectors.toList());
    }

    private List<User> queryUsersByPostsAuthor(List<PostsDO> postsDOS) {
        Set<Long> userIds = SafesUtil.ofList(postsDOS).stream()
                .map(PostsDO::getAuthorId)
                .collect(Collectors.toSet());
        return UserTransfer.toUsers(userDAO.queryInIds(userIds));
    }

    private List<Tag> queryTagsByMapping(List<TagPostsMappingDO> tagPostsMappingDOList) {
        if (ObjectUtils.isEmpty(tagPostsMappingDOList)) {
            return Collections.emptyList();
        }

        Set<Long> tagIds = SafesUtil.ofList(tagPostsMappingDOList).stream()
                .map(TagPostsMappingDO::getTagId)
                .collect(Collectors.toSet());
        return TagTransfer.toTags(tagDAO.queryInIds(tagIds));
    }

    private PageResult<Posts> buildEmptyPageResult(PageInfo pageInfo) {
        return PageResult.build(pageInfo.getTotal(), pageInfo.getSize(), Collections.emptyList());
    }
}
