package pub.developers.forum.domain.repository;

import pub.developers.forum.common.model.PageRequest;
import pub.developers.forum.common.model.PageResult;
import pub.developers.forum.domain.entity.Config;

import java.util.List;
import java.util.Set;

/**
 * Repository interface for managing configuration operations
 * @author Qiangqiang.Bian
 * @create 2020/12/26
 * @desc
 **/
public interface ConfigRepository {

    /**
     * Save a configuration
     * @param config the configuration to save
     */
    void save(Config config);

    /**
     * Get a configuration by ID
     * @param id the configuration ID
     * @return the configuration if found
     */
    Config get(Long id);

    /**
     * Update a configuration
     * @param config the configuration to update
     */
    void update(Config config);

    /**
     * Query available configurations by types
     * @param types the set of configuration types
     * @return list of available configurations
     */
    List<Config> queryAvailable(Set<String> types);

    /**
     * Query configurations by page
     * @param configPageRequest the page request
     * @return page result of configurations
     */
    PageResult<Config> page(PageRequest<Config> configPageRequest);
}