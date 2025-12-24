package pub.developers.forum.api.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * Paginated request model for API endpoints
 * 
 * @author Qiangqiang.Bian
 * @create 20/7/23
 * @desc
 * @param <T> the type of filter criteria
 **/
@Getter
@NoArgsConstructor
public class PageRequestModel<T> implements Serializable {
    // Default page size
    private static final Integer DEF_PAGE_SIZE = 10;
    // Default page number (1-based)
    private static final Integer DEF_PAGE_NO = 1;

    // Number of items per page, defaults to 10
    private Integer pageSize = DEF_PAGE_SIZE;

    // Current page number (1-based), defaults to 1
    private Integer pageNo = DEF_PAGE_NO;

    // Optional filter criteria for the query
    @Setter
    private T filter;

    /**
     * Constructor with pagination and filter parameters
     * 
     * @param pageSize number of items per page
     * @param pageNo page number (1-based)
     * @param filter filter criteria
     */
    public PageRequestModel(Integer pageSize, Integer pageNo, T filter) {
        setPageSize(pageSize);
        setPageNo(pageNo);
        this.filter = filter;
    }

    /**
     * Set page size with validation, defaults to 10 if invalid
     * 
     * @param pageSize number of items per page
     */
    public void setPageSize(Integer pageSize) {
        if (pageSize <= 0) {
            this.pageSize = DEF_PAGE_SIZE;
        } else {
            this.pageSize = pageSize;
        }
    }

    /**
     * Set page number with validation, defaults to 1 if invalid
     * 
     * @param pageNo page number (1-based)
     */
    public void setPageNo(Integer pageNo) {
        if (pageNo <= 0) {
            this.pageNo = DEF_PAGE_NO;
        } else {
            this.pageNo = pageNo;
        }
    }
}