package pub.developers.forum.api.response.article;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pub.developers.forum.api.vo.TagVO;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 文章信息响应对象
 * <p>
 * 用于封装返回给前端的文章详细信息，包括文章基本信息、作者信息、统计数据等
 * 
 * @author Qiangqiang.Bian
 * @create 2020/11/3
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ArticleInfoResponse implements Serializable {

    /**
     * 文章ID
     */
    private Long id;

    /**
     * 文章分类ID
     */
    private Long typeId;

    /**
     * 审核状态
     */
    private String auditState;

    /**
     * 是否官方文章
     */
    private Boolean official;

    /**
     * 是否置顶
     */
    private Boolean top;

    /**
     * 是否精华
     */
    private Boolean marrow;

    /**
     * 文章标题
     */
    private String title;

    /**
     * HTML格式的文章内容
     */
    private String htmlContent;

    /**
     * Markdown格式的文章内容
     */
    private String markdownContent;

    /**
     * 文章头图URL
     */
    private String headImg;

    /**
     * 作者ID
     */
    private Long authorId;

    /**
     * 作者昵称
     */
    private String authorNickname;

    /**
     * 作者头像URL
     */
    private String authorAvatar;

    /**
     * 创建时间
     */
    private Date createAt;

    /**
     * 更新时间
     */
    private Date updateAt;

    /**
     * 浏览次数
     */
    private Long views;

    /**
     * 点赞数
     */
    private Long approvals;

    /**
     * 评论数
     */
    private Long comments;

    /**
     * 文章标签列表
     */
    private List<TagVO> tags;
}
