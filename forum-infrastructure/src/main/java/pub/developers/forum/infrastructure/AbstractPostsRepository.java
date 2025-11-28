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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 抽象文章仓库类
 * 提供文章数据访问的通用方法,主要用于分页查询文章及其关联数据
 *
 * @author Qiangqiang.Bian
 * @create 2020/12/2
 **/
public abstract class AbstractPostsRepository {

    /**
     * 文章数据访问对象
     */
    @Resource
    PostsDAO postsDAO;

    /**
     * 用户数据访问对象
     */
    @Resource
    UserDAO userDAO;

    /**
     * 标签与文章映射关系数据访问对象
     */
    @Resource
    TagPostsMappingDAO tagPostsMappingDAO;

    /**
     * 标签数据访问对象
     */
    @Resource
    TagDAO tagDAO;

    /**
     * 分页查询文章列表的基础方法
     * 根据文章ID列表查询文章详情,并关联查询作者信息和标签信息
     *
     * @param postsIds 文章ID列表,用于指定要查询的文章
     * @param pageInfo 分页信息,包含总数、页码等分页参数
     * @param auditStateEn 审核状态枚举,用于过滤特定审核状态的文章,为空则不过滤
     * @return 包含文章列表的分页结果,其中文章对象包含作者和标签信息
     */
    public PageResult<Posts> basePagePosts(List<Long> postsIds, PageInfo pageInfo, AuditStateEn auditStateEn) {
        // 根据审核状态查询文章数据
        List<PostsDO> queryPostsDOS;
        if (ObjectUtils.isEmpty(auditStateEn)) {
            // 未指定审核状态时,查询所有指定ID的文章
            queryPostsDOS = postsDAO.queryInIds(new HashSet<>(postsIds));
        } else {
            // 指定审核状态时,查询符合该状态的文章
            queryPostsDOS = postsDAO.queryInIdsAndState(new HashSet<>(postsIds), auditStateEn.getValue());
        }

        // 如果未查询到文章数据,返回空列表
        if (ObjectUtils.isEmpty(queryPostsDOS)) {
            return PageResult.build(pageInfo.getTotal(), pageInfo.getSize(), new ArrayList<>());
        }

        // 按照输入的 postsIds 顺序对查询结果进行排序
        // 保持返回结果的顺序与请求的ID列表顺序一致
        List<PostsDO> postsDOS = postsIds.stream().map(postsId -> {
            // 遍历查询结果,找到匹配的文章
            for (PostsDO postsDO : queryPostsDOS) {
                if (postsDO.getId().equals(postsId)) {
                    return postsDO;
                }
            }
            return null;
        }).filter(postsDO -> !ObjectUtils.isEmpty(postsDO)).collect(Collectors.toList());

        // 提取所有文章的作者ID,并查询对应的用户信息
        Set<Long> userIds = SafesUtil.ofList(postsDOS).stream().map(PostsDO::getAuthorId).collect(Collectors.toSet());
        List<User> users = UserTransfer.toUsers(userDAO.queryInIds(userIds));

        // 查询文章与标签的映射关系
        List<TagPostsMappingDO> tagPostsMappingDOList = tagPostsMappingDAO.queryInPostsIds(new HashSet<>(postsIds));
        // 如果文章没有关联标签,直接返回结果(标签列表为空)
        if (ObjectUtils.isEmpty(tagPostsMappingDOList)) {
            return PageResult.build(pageInfo.getTotal(), pageInfo.getSize(), PostsTransfer.toPostsList(postsDOS, users, tagPostsMappingDOList, new ArrayList<>()));
        }

        // 提取所有关联的标签ID,并查询对应的标签信息
        Set<Long> tagIds = SafesUtil.ofList(tagPostsMappingDOList).stream().map(TagPostsMappingDO::getTagId).collect(Collectors.toSet());
        List<Tag> tags = TagTransfer.toTags(tagDAO.queryInIds(tagIds));

        // 组装最终结果:将文章、作者、标签映射关系、标签信息整合后返回
        return PageResult.build(pageInfo.getTotal(), pageInfo.getSize(), PostsTransfer.toPostsList(postsDOS, users, tagPostsMappingDOList, tags));
    }
}
