package pub.developers.forum.facade.impl;

import org.springframework.stereotype.Service;
import pub.developers.forum.api.model.PageRequestModel;
import pub.developers.forum.api.model.PageResponseModel;
import pub.developers.forum.api.model.ResultModel;
import pub.developers.forum.api.request.AdminBooleanRequest;
import pub.developers.forum.api.request.config.ConfigAddRequest;
import pub.developers.forum.api.request.config.ConfigPageRequest;
import pub.developers.forum.api.request.config.ConfigUpdateRequest;
import pub.developers.forum.api.response.config.ConfigResponse;
import pub.developers.forum.api.service.ConfigApiService;
import pub.developers.forum.app.manager.ConfigManager;
import pub.developers.forum.facade.support.ResultModelUtil;
import pub.developers.forum.facade.validator.PageRequestModelValidator;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;

/**
 * Configuration API Service Implementation
 * Manages system configuration operations including add, update, state management, and querying
 * 
 * @author Qiangqiang.Bian
 * @create 2020/12/26
 **/
@Service
public class ConfigApiServiceImpl implements ConfigApiService {

    @Resource
    private ConfigManager configManager;

    /**
     * Add a new configuration entry
     * 
     * @param request configuration add request
     * @return success result
     */
    @Override
    public ResultModel add(ConfigAddRequest request) {

        configManager.add(request);

        return ResultModelUtil.success();
    }

    /**
     * Update an existing configuration entry
     * 
     * @param request configuration update request
     * @return success result
     */
    @Override
    public ResultModel update(ConfigUpdateRequest request) {

        configManager.update(request);

        return ResultModelUtil.success();
    }

    /**
     * Update configuration state (enable/disable)
     * 
     * @param request boolean request with config ID and state
     * @return success result
     */
    @Override
    public ResultModel state(AdminBooleanRequest request) {

        configManager.state(request);

        return ResultModelUtil.success();
    }

    /**
     * Get paginated list of configurations for admin management
     * 
     * @param pageRequestModel page request with config filter
     * @return paginated configuration list
     */
    @Override
    public ResultModel<PageResponseModel<ConfigResponse>> page(PageRequestModel<ConfigPageRequest> pageRequestModel) {
        PageRequestModelValidator.validator(pageRequestModel);

        return ResultModelUtil.success(configManager.page(pageRequestModel));
    }

    /**
     * Query available configurations by types
     * Returns only enabled/available configurations
     * 
     * @param types set of configuration types to query
     * @return list of available configurations
     */
    @Override
    public ResultModel<List<ConfigResponse>> queryAvailable(Set<String> types) {

        return ResultModelUtil.success(configManager.queryAvailable(types));
    }
}