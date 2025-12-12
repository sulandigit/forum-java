package pub.developers.forum.infrastructure.dal.dataobject;

import lombok.Data;

@Data
public class CommentWithUserDO extends BaseDO {
    private Long userId;
    private Long replyId;
    private Long replyReplyId;
    private Long postsId;
    private String content;
    
    private String userNickname;
    private String userAvatar;
}
