package pub.developers.forum.domain.repository;

import pub.developers.forum.common.enums.ArticleTypeScopeEn;
import pub.developers.forum.common.enums.AuditStateEn;
import pub.developers.forum.common.model.PageRequest;
import pub.developers.forum.common.model.PageResult;
import pub.developers.forum.domain.entity.ArticleType;

import java.util.List;

/**
 * Repository interface for managing article type operations
 * @author Qiangqiang.Bian
 * @create 2020/10/31
 * @desc
 **/
public interface ArticleTypeRepository {

    /**
     * Save an article type
     * @param articleType the article type to save
     */
    void save(ArticleType articleType);

    /**
     * Query article types by criteria
     * @param articleType the query criteria
     * @return list of article types
     */
    List<ArticleType> query(ArticleType articleType);

    /**
     * Query article types by audit state
     * @param auditState the audit state
     * @return list of article types
     */
    List<ArticleType> queryByState(AuditStateEn auditState);

    /**
     * Query article types by scopes and state
     * @param scopes the list of scopes
     * @param auditState the audit state
     * @return list of article types
     */
    List<ArticleType> queryByScopesAndState(List<ArticleTypeScopeEn> scopes, AuditStateEn auditState);

    /**
     * Update an article type
     * @param articleType the article type to update
     */
    void update(ArticleType articleType);

    /**
     * Get an article type by ID
     * @param id the article type ID
     * @return the article type if found
     */
    ArticleType get(Long id);

    /**
     * Get an article type by name and state
     * @param typeName the article type name
     * @param pass the audit state
     * @return the article type if found
     */
    ArticleType getByNameAndState(String typeName, AuditStateEn pass);

    /**
     * Increase the reference count of an article type
     * @param id the article type ID
     */
    void increaseRefCount(Long id);

    /**
     * Decrease the reference count of an article type
     * @param id the article type ID
     */
    void decreaseRefCount(Long id);

    /**
     * Query article types by page
     * @param articleTypePageRequest the page request
     * @return page result of article types
     */
    PageResult<ArticleType> page(PageRequest<ArticleType> articleTypePageRequest);
}