package pub.developers.forum.domain.repository;

import pub.developers.forum.common.enums.MessageTypeEn;
import pub.developers.forum.common.model.PageRequest;
import pub.developers.forum.common.model.PageResult;
import pub.developers.forum.domain.entity.Message;

import java.util.List;

/**
 * @author Qiangqiang.Bian
 * @create 2020/10/22
 * @desc
 **/
public interface MessageRepository {

    void save(Message message);

    /**
     * 批量保存消息
     * @param messages 消息列表
     */
    void batchSave(List<Message> messages);

    Message get(Long id);

    PageResult<Message> page(PageRequest<Message> pageRequest);

    void updateToRead(Message message);

    Long countUnRead(Long receiver);

    void deleteInTypesAndTitle(List<MessageTypeEn> typeEns, String title);
}
