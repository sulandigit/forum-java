package pub.developers.forum.domain.repository;

import pub.developers.forum.common.model.PageResult;
import pub.developers.forum.domain.entity.Article;
import pub.developers.forum.domain.entity.value.PostsPageQueryValue;

/**
 * Repository interface for managing article operations
 * @author Qiangqiang.Bian
 * @create 2020/10/31
 * @desc
 **/
public interface ArticleRepository {

    /**
     * Save an article
     * @param article the article to save
     */
    void save(Article article);

    /**
     * Get an article by ID
     * @param id the article ID
     * @return the article if found
     */
    Article get(Long id);

    /**
     * Update an article
     * @param article the article to update
     */
    void update(Article article);

    /**
     * Query articles by page
     * @param pageNo the page number
     * @param pageSize the page size
     * @param pageQueryValue the query criteria
     * @return page result of articles
     */
    PageResult<Article> page(Integer pageNo, Integer pageSize, PostsPageQueryValue pageQueryValue);
}