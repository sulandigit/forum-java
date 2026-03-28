package pub.developers.forum.app.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pub.developers.forum.common.enums.SearchTypeEn;
import pub.developers.forum.common.support.EventBus;
import pub.developers.forum.common.support.SearchIndexExecutorService;
import pub.developers.forum.domain.entity.Article;
import pub.developers.forum.domain.entity.Search;
import pub.developers.forum.domain.service.SearchService;

import javax.annotation.Resource;

/**
 * @author Qiangqiang.Bian
 * @create 2020/12/2
 * @desc 文章创建搜索索引监听器 - 异步处理
 **/
@Slf4j
@Component
public class SearchArticleCreateListener extends EventBus.EventHandler<Article> {

    @Resource
    private SearchService searchService;

    @Resource
    private SearchIndexExecutorService searchIndexExecutorService;

    @Override
    public EventBus.Topic topic() {
        return EventBus.Topic.ARTICLE_CREATE;
    }

    @Override
    public void onMessage(Article article) {
        // 提交到搜索索引异步执行器
        searchIndexExecutorService.submitTask(() -> {
            log.info("[搜索索引] 文章创建索引更新 - 文章ID:{}, 标题:{}", article.getId(), article.getTitle());
            
            searchService.deleteByPostsId(article.getId());

            searchService.save(Search.builder()
                    .content(article.getMarkdownContent())
                    .entityId(article.getId())
                    .title(article.getTitle())
                    .type(SearchTypeEn.POSTS)
                    .build());
        });
    }
}
