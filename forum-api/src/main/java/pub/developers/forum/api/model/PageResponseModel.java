package pub.developers.forum.api.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * Paginated response model for API endpoints
 * 
 * @author Qiangqiang.Bian
 * @create 20/7/23
 * @desc
 * @param <T> the type of items in the list
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PageResponseModel<T> implements Serializable {

    // List of items for the current page
    private List<T> list;
    // Total number of items across all pages
    private Long total;
    // Number of items per page
    private Integer size;

    /**
     * Factory method to build a PageResponseModel
     * 
     * @param total total number of items
     * @param size number of items per page
     * @param list list of items for current page
     * @param <T> the type of items
     * @return a new PageResponseModel instance
     */
    public static <T> PageResponseModel<T> build(Long total, Integer size, List<T> list) {
        PageResponseModel<T> result = new PageResponseModel<>();
        result.setSize(size);
        result.setTotal(total);
        result.setList(list);

        return result;
    }

}