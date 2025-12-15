package pub.developers.forum.domain.repository;

import pub.developers.forum.common.model.PageResult;
import pub.developers.forum.domain.entity.Article;
import pub.developers.forum.domain.entity.value.PostsPageQueryValue;

/**
 * Repository interface for Article entity operations
 * 
 * @author Qiangqiang.Bian
 * @create 2020/10/31
 * @desc
 **/
public interface ArticleRepository {

    /**
     * Save a new article to the repository
     * 
     * @param article the article to save
     */
    void save(Article article);

    /**
     * Retrieve an article by its ID
     * 
     * @param id the article ID
     * @return the article entity
     */
    Article get(Long id);

    /**
     * Update an existing article
     * 
     * @param article the article to update
     */
    void update(Article article);

    /**
     * Retrieve a paginated list of articles
     * 
     * @param pageNo the page number
     * @param pageSize the page size
     * @param pageQueryValue the query parameters for filtering
     * @return paginated result containing articles
     */
    PageResult<Article> page(Integer pageNo, Integer pageSize, PostsPageQueryValue pageQueryValue);
}