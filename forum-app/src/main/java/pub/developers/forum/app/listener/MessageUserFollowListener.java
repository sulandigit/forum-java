package pub.developers.forum.app.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pub.developers.forum.app.support.Pair;
import pub.developers.forum.common.enums.*;
import pub.developers.forum.common.support.EventBus;
import pub.developers.forum.common.support.MessageExecutorService;
import pub.developers.forum.domain.entity.Message;
import pub.developers.forum.domain.entity.value.IdValue;

import javax.annotation.Resource;

/**
 * @author Qiangqiang.Bian
 * @create 2020/12/5
 * @desc 用户关注消息通知监听器 - 异步处理
 **/
@Slf4j
@Component
public class MessageUserFollowListener extends EventBus.EventHandler<Pair<Long>> {

    @Resource
    private MessageExecutorService messageExecutorService;

    @Override
    public EventBus.Topic topic() {
        return EventBus.Topic.USER_FOLLOW;
    }

    @Override
    public void onMessage(Pair<Long> pair) {
        Long followed = pair.getValue0();
        Long follower = pair.getValue1();

        Message message = Message.builder()
                .channel(MessageChannelEn.STATION_LETTER)
                .type(MessageTypeEn.FOLLOW_USER)
                .receiver(IdValue.builder()
                        .id(followed.toString())
                        .type(IdValueTypeEn.USER_ID)
                        .build())
                .read(MessageReadEn.NO)
                .contentType(MessageContentTypeEn.TEXT)
                .content("")
                .sender(IdValue.builder()
                        .id(follower.toString())
                        .type(IdValueTypeEn.USER_ID)
                        .build())
                .title("")
                .build();
        
        log.info("[消息通知] 关注通知 - 被关注人:{}, 关注人:{}", followed, follower);
        // 提交到消息异步执行器
        messageExecutorService.submitMessageTask(message);
    }
}
