package pub.developers.forum.app.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pub.developers.forum.app.support.Pair;
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
 * @desc 文章更新搜索索引监听器 - 异步处理
 **/
@Slf4j
@Component
public class SearchArticleUpdateListener  extends EventBus.EventHandler<Pair<Article>> {

    @Resource
    private SearchService searchService;

    @Resource
    private SearchIndexExecutorService searchIndexExecutorService;

    @Override
    public EventBus.Topic topic() {
        return EventBus.Topic.ARTICLE_UPDATE;
    }

    @Override
    public void onMessage(Pair<Article> pair) {
        Article newArticle = pair.getValue1();

        // 提交到搜索索引异步执行器
        searchIndexExecutorService.submitTask(() -> {
            log.info("[搜索索引] 文章更新索引更新 - 文章ID:{}, 标题:{}", newArticle.getId(), newArticle.getTitle());
            
            searchService.deleteByPostsId(newArticle.getId());

            searchService.save(Search.builder()
                    .content(newArticle.getMarkdownContent())
                    .entityId(newArticle.getId())
                    .title(newArticle.getTitle())
                    .type(SearchTypeEn.POSTS)
                    .build());
        });
    }
}
