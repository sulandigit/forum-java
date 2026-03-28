package pub.developers.forum.infrastructure;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pub.developers.forum.common.enums.MessageChannelEn;
import pub.developers.forum.common.support.MessageExecutorService;
import pub.developers.forum.domain.entity.Message;
import pub.developers.forum.domain.repository.MessageRepository;
import pub.developers.forum.domain.service.MessageService;

import javax.annotation.Resource;

/**
 * @author Qiangqiang.Bian
 * @create 2020/10/22
 * @desc 消息服务 - 异步处理
 **/
@Slf4j
@Service
public class MessageServiceImpl implements MessageService {

    @Resource
    private MessageRepository messageRepository;

    @Resource
    private MessageExecutorService messageExecutorService;

    @Override
    public void send(Message message) {
        log.info("[消息服务] 提交消息异步处理 - 渠道:{}, 类型:{}", message.getChannel(), message.getType());
        
        // 提交到消息异步执行器
        messageExecutorService.submitMessageTask(message);
        
        // 邮件渠道需要保存记录，这里同步保存
        // 站内信由批量处理器异步保存
        if (MessageChannelEn.MAIL.equals(message.getChannel())) {
            messageRepository.save(message);
        }
    }

}
