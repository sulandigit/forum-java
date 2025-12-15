package pub.developers.forum.domain.repository;

import pub.developers.forum.common.model.PageRequest;
import pub.developers.forum.common.model.PageResult;
import pub.developers.forum.domain.entity.Config;

import java.util.List;
import java.util.Set;

public interface ConfigRepository {

    void save(Config config);

    Config get(Long id);

    void update(Config config);

    List<Config> queryAvailable(Set<String> types);

    PageResult<Config> page(PageRequest<Config> configPageRequest);
}