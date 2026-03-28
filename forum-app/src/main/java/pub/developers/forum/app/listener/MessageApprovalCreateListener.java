package pub.developers.forum.app.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import pub.developers.forum.app.support.Pair;
import pub.developers.forum.common.enums.*;
import pub.developers.forum.common.support.EventBus;
import pub.developers.forum.common.support.MessageExecutorService;
import pub.developers.forum.domain.entity.BasePosts;
import pub.developers.forum.domain.entity.Message;
import pub.developers.forum.domain.entity.value.IdValue;
import pub.developers.forum.domain.repository.PostsRepository;

import javax.annotation.Resource;

/**
 * @author Qiangqiang.Bian
 * @create 2020/12/5
 * @desc 点赞消息通知监听器 - 异步处理
 **/
@Slf4j
@Component
public class MessageApprovalCreateListener extends EventBus.EventHandler<Pair<Long>> {

    @Resource
    private MessageExecutorService messageExecutorService;

    @Resource
    private PostsRepository postsRepository;

    @Override
    public EventBus.Topic topic() {
        return EventBus.Topic.APPROVAL_CREATE;
    }

    @Override
    public void onMessage(Pair<Long> pair) {
        Long userId = pair.getValue0();
        Long postsId = pair.getValue1();

        BasePosts basePosts = postsRepository.get(postsId);
        if (ObjectUtils.isEmpty(basePosts)) {
            return;
        }

        Message message = Message.builder()
                .channel(MessageChannelEn.STATION_LETTER)
                .receiver(IdValue.builder()
                        .id(basePosts.getAuthorId().toString())
                        .type(IdValueTypeEn.USER_ID)
                        .build())
                .read(MessageReadEn.NO)
                .contentType(MessageContentTypeEn.TEXT)
                .title(postsId.toString())
                .content("")
                .sender(IdValue.builder()
                        .id(userId.toString())
                        .type(IdValueTypeEn.USER_ID)
                        .build())
                .build();
        if (PostsCategoryEn.ARTICLE.equals(basePosts.getCategory())) {
            message.setType(MessageTypeEn.APPROVAL_ARTICLE);
        } else if (PostsCategoryEn.FAQ.equals(basePosts.getCategory())) {
            message.setType(MessageTypeEn.APPROVAL_FAQ);
        } else {
            return;
        }

        log.info("[消息通知] 点赞通知 - 帖子ID:{}, 接收人:{}", postsId, basePosts.getAuthorId());
        // 提交到消息异步执行器
        messageExecutorService.submitMessageTask(message);
    }
}
