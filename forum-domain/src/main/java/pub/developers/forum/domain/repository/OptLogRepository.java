package pub.developers.forum.domain.repository;

import pub.developers.forum.common.model.PageRequest;
import pub.developers.forum.common.model.PageResult;
import pub.developers.forum.domain.entity.OptLog;

/**
 * Repository interface for managing operation log operations
 * @author Qiangqiang.Bian
 * @create 2020/10/20
 * @desc
 **/
public interface OptLogRepository {

    /**
     * Save an operation log
     * @param optLog the operation log to save
     */
    void save(OptLog optLog);

    /**
     * Query operation logs by page
     * @param pageRequest the page request
     * @return page result of operation logs
     */
    PageResult<OptLog> page(PageRequest<OptLog> pageRequest);
}