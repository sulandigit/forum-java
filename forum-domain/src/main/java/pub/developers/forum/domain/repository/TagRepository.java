package pub.developers.forum.domain.repository;

import pub.developers.forum.common.enums.AuditStateEn;
import pub.developers.forum.common.enums.PostsCategoryEn;
import pub.developers.forum.common.model.PageRequest;
import pub.developers.forum.common.model.PageResult;
import pub.developers.forum.domain.entity.Posts;
import pub.developers.forum.domain.entity.Tag;

import java.util.List;
import java.util.Set;

/**
 * Repository interface for managing tag operations
 * @author Qiangqiang.Bian
 * @create 2020/7/31
 * @desc
 **/
public interface TagRepository {

    /**
     * Save a tag
     * @param tag the tag to save
     */
    void save(Tag tag);

    /**
     * Query tags by criteria
     * @param tag the query criteria
     * @return list of tags
     */
    List<Tag> query(Tag tag);

    /**
     * Query tags by IDs
     * @param ids the set of tag IDs
     * @return list of tags
     */
    List<Tag> queryByIds(Set<Long> ids);

    /**
     * Query tags by audit state
     * @param auditState the audit state
     * @return list of tags
     */
    List<Tag> queryByState(AuditStateEn auditState);

    /**
     * Delete posts mapping for an article
     * @param articleId the article ID
     */
    void deletePostsMapping(Long articleId);

    /**
     * Increase reference count for tags
     * @param ids the set of tag IDs
     */
    void increaseRefCount(Set<Long> ids);

    /**
     * Decrease reference count for tags
     * @param ids the set of tag IDs
     */
    void decreaseRefCount(Set<Long> ids);

    /**
     * Get a tag by name and state
     * @param name the tag name
     * @param pass the audit state
     * @return the tag if found
     */
    Tag getByNameAndState(String name, AuditStateEn pass);

    /**
     * Query posts by page for a tag
     * @param longPageRequest the page request with tag ID
     * @return page result of posts
     */
    PageResult<Posts> pagePosts(PageRequest<Long> longPageRequest);

    /**
     * Query posts by page for multiple tags
     * @param pageRequest the page request with tag IDs
     * @return page result of posts
     */
    PageResult<Posts> pagePostsByTagIds(PageRequest<Set<Long>> pageRequest);

    /**
     * Query tags by page
     * @param tagPageRequest the page request
     * @return page result of tags
     */
    PageResult<Tag> page(PageRequest<Tag> tagPageRequest);

    /**
     * Get a tag by ID
     * @param id the tag ID
     * @return the tag if found
     */
    Tag get(Long id);

    /**
     * Update a tag
     * @param tag the tag to update
     */
    void update(Tag tag);
}