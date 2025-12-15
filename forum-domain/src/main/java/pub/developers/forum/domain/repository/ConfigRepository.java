package pub.developers.forum.domain.repository;

import pub.developers.forum.common.model.PageRequest;
import pub.developers.forum.common.model.PageResult;
import pub.developers.forum.domain.entity.Config;

import java.util.List;
import java.util.Set;

/**
 * @author Qiangqiang.Bian
 * @create 2020/12/26
 * @desc
 **/
public interface ConfigRepository {

    /**
     * Save configuration
     * @param config
     */
    void save(Config config);

    /**
     * Get configuration by ID
     * @param id
     * @return
     */
    Config get(Long id);

    /**
     * Update configuration
     * @param config
     */
    void update(Config config);

    /**
     * Query available configurations by types
     * @param types
     * @return
     */
    List<Config> queryAvailable(Set<String> types);

    /**
     * Query configurations by page
     * @param configPageRequest
     * @return
     */
    PageResult<Config> page(PageRequest<Config> configPageRequest);
}