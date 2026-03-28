package pub.developers.forum.app.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
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
 * @desc FAQ创建搜索索引监听器 - 异步处理
 **/
@Slf4j
@Component
public class SearchFaqCreateListener extends EventBus.EventHandler<Faq> {

    @Resource
    private SearchService searchService;

    @Resource
    private SearchIndexExecutorService searchIndexExecutorService;

    @Override
    public EventBus.Topic topic() {
        return EventBus.Topic.FAQ_CREATE;
    }

    @Override
    public void onMessage(Faq faq) {
        // 提交到搜索索引异步执行器
        searchIndexExecutorService.submitTask(() -> {
            log.info("[搜索索引] FAQ创建索引更新 - FAQID:{}, 标题:{}", faq.getId(), faq.getTitle());
            
            searchService.deleteByPostsId(faq.getId());

            searchService.save(Search.builder()
                    .content(faq.getMarkdownContent())
                    .entityId(faq.getId())
                    .title(faq.getTitle())
                    .type(SearchTypeEn.POSTS)
                    .build());
        });
    }
}
