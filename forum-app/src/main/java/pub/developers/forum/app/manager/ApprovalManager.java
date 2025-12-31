package pub.developers.forum.app.manager;

import org.springframework.stereotype.Component;
import pub.developers.forum.app.support.IsLogin;
import pub.developers.forum.app.support.LoginUserContext;
import pub.developers.forum.app.support.Pair;
import pub.developers.forum.common.enums.ErrorCodeEn;
import pub.developers.forum.common.support.CheckUtil;
import pub.developers.forum.common.support.EventBus;
import pub.developers.forum.domain.entity.Approval;
import pub.developers.forum.domain.entity.BasePosts;
import pub.developers.forum.domain.repository.ApprovalRepository;
import pub.developers.forum.domain.repository.PostsRepository;

import javax.annotation.Resource;

/**
 * 点赞管理器
 * 
 * @author Qiangqiang.Bian
 * @create 2020/12/1
 * @desc
 **/
@Component
public class ApprovalManager {

    @Resource
    private ApprovalRepository approvalRepository;

    @Resource
    private PostsRepository postsRepository;

    /**
     * 创建点赞
     * 
     * @param postsId 帖子ID
     * @return 点赞数
     */
    @IsLogin
    public Long create(Long postsId) {
        // 检查是否已点赞，避免重复操作
        Approval approval = approvalRepository.get(postsId, LoginUserContext.getUser().getId());
        CheckUtil.isNotEmpty(approval, ErrorCodeEn.REPEAT_OPERATION);

        // 验证帖子是否存在
        BasePosts basePosts = postsRepository.get(postsId);
        CheckUtil.isEmpty(basePosts, ErrorCodeEn.POSTS_NOT_EXIST);

        // 保存点赞记录
        approvalRepository.save(Approval.builder()
                .postsId(postsId)
                .userId(LoginUserContext.getUser().getId())
                .build());
        // 增加帖子点赞数
        postsRepository.increaseApproval(postsId, basePosts.getUpdateAt());

        // 发布点赞创建事件
        EventBus.emit(EventBus.Topic.APPROVAL_CREATE, Pair.build(LoginUserContext.getUser().getId(), postsId));

        return basePosts.getApprovals() + 1;
    }

    /**
     * 删除点赞
     * 
     * @param postsId 帖子ID
     * @return 点赞数
     */
    @IsLogin
    public Long delete(Long postsId) {
        // 检查点赞记录是否存在
        Approval approval = approvalRepository.get(postsId, LoginUserContext.getUser().getId());
        CheckUtil.isEmpty(approval, ErrorCodeEn.OPERATION_DATA_NOT_EXIST);

        // 验证帖子是否存在
        BasePosts basePosts = postsRepository.get(postsId);
        CheckUtil.isEmpty(basePosts, ErrorCodeEn.POSTS_NOT_EXIST);

        // 删除点赞记录
        approvalRepository.delete(approval.getId());
        // 减少帖子点赞数
        postsRepository.decreaseApproval(postsId, basePosts.getUpdateAt());

        return basePosts.getApprovals() - 1;
    }

    /**
     * 检查用户是否已点赞
     * 
     * @param postsId 帖子ID
     * @return 是否已点赞
     */
    @IsLogin
    public Boolean hasApproval(Long postsId) {
        Approval approval = approvalRepository.get(postsId, LoginUserContext.getUser().getId());

        return approval != null;
    }

}