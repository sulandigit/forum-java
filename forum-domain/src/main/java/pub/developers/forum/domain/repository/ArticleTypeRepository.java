package pub.developers.forum.domain.repository;

import pub.developers.forum.common.enums.ArticleTypeScopeEn;
import pub.developers.forum.common.enums.AuditStateEn;
import pub.developers.forum.common.model.PageRequest;
import pub.developers.forum.common.model.PageResult;
import pub.developers.forum.domain.entity.ArticleType;

import java.util.List;

/**
 * Repository interface for ArticleType entity operations
 * 
 * @author Qiangqiang.Bian
 * @create 2020/10/31
 * @desc
 **/
public interface ArticleTypeRepository {

    /**
     * Save a new article type to the repository
     * 
     * @param articleType the article type to save
     */
    void save(ArticleType articleType);

    /**
     * Query article types by matching criteria
     * 
     * @param articleType the article type with query criteria
     * @return list of matching article types
     */
    List<ArticleType> query(ArticleType articleType);

    /**
     * Query article types by audit state
     * 
     * @param auditState the audit state to filter by
     * @return list of article types with the specified state
     */
    List<ArticleType> queryByState(AuditStateEn auditState);

    /**
     * Query article types by scopes and audit state
     * 
     * @param scopes list of article type scopes
     * @param auditState the audit state to filter by
     * @return list of article types matching the criteria
     */
    List<ArticleType> queryByScopesAndState(List<ArticleTypeScopeEn> scopes, AuditStateEn auditState);

    /**
     * Update an existing article type
     * 
     * @param articleType the article type to update
     */
    void update(ArticleType articleType);

    /**
     * Retrieve an article type by its ID
     * 
     * @param id the article type ID
     * @return the article type entity
     */
    ArticleType get(Long id);

    /**
     * Retrieve an article type by name and state
     * 
     * @param typeName the article type name
     * @param pass the audit state
     * @return the article type entity
     */
    ArticleType getByNameAndState(String typeName, AuditStateEn pass);

    /**
     * Increase the reference count of an article type
     * 
     * @param id the article type ID
     */
    void increaseRefCount(Long id);

    /**
     * Decrease the reference count of an article type
     * 
     * @param id the article type ID
     */
    void decreaseRefCount(Long id);

    /**
     * Retrieve a paginated list of article types
     * 
     * @param articleTypePageRequest the page request with query parameters
     * @return paginated result containing article types
     */
    PageResult<ArticleType> page(PageRequest<ArticleType> articleTypePageRequest);
}