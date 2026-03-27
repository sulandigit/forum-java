package pub.developers.forum.app.listener;

import org.springframework.stereotype.Component;
import pub.developers.forum.common.support.EventBus;
import pub.developers.forum.domain.entity.Article;
import pub.developers.forum.domain.entity.Tag;
import pub.developers.forum.domain.repository.ArticleTypeRepository;
import pub.developers.forum.domain.repository.TagRepository;

import javax.annotation.Resource;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 文章创建事件监听器
 * 负责在文章创建后更新标签和文章类型的引用计数
 * 
 * @author Qiangqiang.Bian
 * @create 2020/11/4
 * @desc
 **/
@Component
public class ArticleCreateListener extends EventBus.EventHandler<Article> {

    @Resource
    private TagRepository tagRepository;

    @Resource
    private ArticleTypeRepository articleTypeRepository;

    /**
     * 订阅的事件主题
     * 
     * @return 返回文章创建事件主题
     */
    @Override
    public EventBus.Topic topic() {
        return EventBus.Topic.ARTICLE_CREATE;
    }

    /**
     * 处理文章创建事件
     * 当文章创建成功后,增加关联标签和文章类型的引用计数
     * 
     * @param article 创建的文章实体
     */
    @Override
    public void onMessage(Article article) {
        // 提取文章的所有标签ID
        Set<Long> tagIds = article.getTags().stream().map(Tag::getId).collect(Collectors.toSet());
        // 增加标签的引用计数
        tagRepository.increaseRefCount(tagIds);

        // 增加文章类型的引用计数
        articleTypeRepository.increaseRefCount(article.getType().getId());
    }
}