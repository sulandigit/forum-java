package pub.developers.forum.app.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pub.developers.forum.common.support.EventBus;
import pub.developers.forum.common.support.SearchIndexExecutorService;
import pub.developers.forum.domain.entity.BasePosts;
import pub.developers.forum.domain.service.SearchService;

import javax.annotation.Resource;

/**
 * @author Qiangqiang.Bian
 * @create 2020/12/3
 * @desc 帖子删除搜索索引监听器 - 异步处理
 **/
@Slf4j
@Component
public class SearchPostsDeleteListener extends EventBus.EventHandler<BasePosts> {

    @Resource
    private SearchService searchService;

    @Resource
    private SearchIndexExecutorService searchIndexExecutorService;

    @Override
    public EventBus.Topic topic() {
        return EventBus.Topic.POSTS_DELETE;
    }

    @Override
    public void onMessage(BasePosts basePosts) {
        // 提交到搜索索引异步执行器
        searchIndexExecutorService.submitTask(() -> {
            log.info("[搜索索引] 帖子删除索引清理 - 帖子ID:{}", basePosts.getId());
            searchService.deleteByPostsId(basePosts.getId());
        });
    }
}
