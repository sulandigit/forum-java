package pub.developers.forum.domain.repository;

import pub.developers.forum.common.model.PageResult;
import pub.developers.forum.domain.entity.Faq;
import pub.developers.forum.domain.entity.value.PostsPageQueryValue;

import java.util.List;

/**
 * Repository interface for managing FAQ operations
 * @author Qiangqiang.Bian
 * @create 2020/11/1
 * @desc
 **/
public interface FaqRepository {

    /**
     * Save a FAQ
     * @param faq the FAQ to save
     */
    void save(Faq faq);

    /**
     * Update a FAQ
     * @param faq the FAQ to update
     */
    void update(Faq faq);

    /**
     * Update a FAQ entity
     * @param faq the FAQ entity to update
     */
    void updateEntity(Faq faq);

    /**
     * Get a FAQ by ID
     * @param id the FAQ ID
     * @return the FAQ if found
     */
    Faq get(Long id);

    /**
     * Query FAQs by page
     * @param pageNo the page number
     * @param pageSize the page size
     * @param pageQueryValue the query criteria
     * @return page result of FAQs
     */
    PageResult<Faq> page(Integer pageNo, Integer pageSize, PostsPageQueryValue pageQueryValue);

    /**
     * Get hot FAQs
     * @param size the number of FAQs to retrieve
     * @return list of hot FAQs
     */
    List<Faq> hots(int size);

}