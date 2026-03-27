package pub.developers.forum.app.listener;

import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import pub.developers.forum.app.support.Pair;
import pub.developers.forum.common.support.EventBus;
import pub.developers.forum.domain.entity.Article;
import pub.developers.forum.domain.repository.ArticleTypeRepository;
import pub.developers.forum.domain.repository.TagRepository;

import javax.annotation.Resource;
import java.util.Set;

/**
 * @author Qiangqiang.Bian
 * @create 2020/11/5
 * @desc Listener for article update events, responsible for maintaining reference counts
 *       for article types and tags when an article is updated.
 **/
@Component
public class ArticleUpdateListener extends EventBus.EventHandler<Pair<Article>> {

    // Repository for managing article type reference counts
    @Resource
    private ArticleTypeRepository articleTypeRepository;

    // Repository for managing tag reference counts
    @Resource
    private TagRepository tagRepository;

    /**
     * Specifies the event topic this listener subscribes to.
     * @return Topic.ARTICLE_UPDATE event topic
     */
    @Override
    public EventBus.Topic topic() {
        return EventBus.Topic.ARTICLE_UPDATE;
    }

    /**
     * Handles article update events by updating reference counts for article types and tags.
     * Processes changes in article types and tags, updating their reference counts accordingly.
     * 
     * @param pair A pair containing the old article and new article
     */
    @Override
    public void onMessage(Pair<Article> pair) {
        Article oldArticle = pair.getValue0();
        Article newArticle = pair.getValue1();

        // Update article type reference count: decrease the old type's count and increase the new type's count
        if (!oldArticle.getType().equals(newArticle.getType())) {
            articleTypeRepository.decreaseRefCount(oldArticle.getType().getId());
            articleTypeRepository.increaseRefCount(newArticle.getType().getId());
        }

        // Restore the old tags' reference counts because ArticleManager has already decreased them once
        Set<Long> oldTags=Pair.tagToLong(oldArticle.getTags());
        tagRepository.increaseRefCount(oldTags);

        // Update tag reference counts: calculate added and removed tags, then update their counts
        Set<Long> addTags = Pair.diff(newArticle.getTags(), oldArticle.getTags());
        Set<Long> removeTags = Pair.diff(oldArticle.getTags(), newArticle.getTags());
        if (!ObjectUtils.isEmpty(addTags)) {
            tagRepository.increaseRefCount(addTags);
        }
        if (!ObjectUtils.isEmpty(removeTags)) {
            tagRepository.decreaseRefCount(removeTags);
        }
    }

}