package pub.developers.forum.app.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pub.developers.forum.app.support.Pair;
import pub.developers.forum.common.enums.SearchTypeEn;
import pub.developers.forum.common.support.EventBus;
import pub.developers.forum.common.support.SearchIndexExecutorService;
import pub.developers.forum.domain.entity.Faq;
import pub.developers.forum.domain.entity.Search;
import pub.developers.forum.domain.service.SearchService;

import javax.annotation.Resource;

/**
 * @author Qiangqiang.Bian
 * @create 2020/12/2
 * @desc FAQ更新搜索索引监听器 - 异步处理
 **/
@Slf4j
@Component
public class SearchFaqUpdateListener extends EventBus.EventHandler<Pair<Faq>> {

    @Resource
    private SearchService searchService;

    @Resource
    private SearchIndexExecutorService searchIndexExecutorService;

    @Override
    public EventBus.Topic topic() {
        return EventBus.Topic.FAQ_UPDATE;
    }

    @Override
    public void onMessage(Pair<Faq> pair) {
        Faq newFaq = pair.getValue1();

        // 提交到搜索索引异步执行器
        searchIndexExecutorService.submitTask(() -> {
            log.info("[搜索索引] FAQ更新索引更新 - FAQID:{}, 标题:{}", newFaq.getId(), newFaq.getTitle());
            
            searchService.deleteByPostsId(newFaq.getId());

            searchService.save(Search.builder()
                    .content(newFaq.getMarkdownContent())
                    .entityId(newFaq.getId())
                    .title(newFaq.getTitle())
                    .type(SearchTypeEn.POSTS)
                    .build());
        });
    }
}
