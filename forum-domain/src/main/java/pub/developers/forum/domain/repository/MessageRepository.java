package pub.developers.forum.domain.repository;

import pub.developers.forum.common.enums.MessageTypeEn;
import pub.developers.forum.common.model.PageRequest;
import pub.developers.forum.common.model.PageResult;
import pub.developers.forum.domain.entity.Message;

import java.util.List;

/**
 * Repository interface for managing message operations
 * @author Qiangqiang.Bian
 * @create 2020/10/22
 * @desc
 **/
public interface MessageRepository {

    /**
     * Save a message
     * @param message the message to save
     */
    void save(Message message);

    /**
     * Get a message by ID
     * @param id the message ID
     * @return the message if found
     */
    Message get(Long id);

    /**
     * Query messages by page
     * @param pageRequest the page request
     * @return page result of messages
     */
    PageResult<Message> page(PageRequest<Message> pageRequest);

    /**
     * Update message status to read
     * @param message the message to mark as read
     */
    void updateToRead(Message message);

    /**
     * Count unread messages for a receiver
     * @param receiver the receiver user ID
     * @return count of unread messages
     */
    Long countUnRead(Long receiver);

    /**
     * Delete messages by types and title
     * @param typeEns the list of message types
     * @param title the message title
     */
    void deleteInTypesAndTitle(List<MessageTypeEn> typeEns, String title);
}