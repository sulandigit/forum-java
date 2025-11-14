package pub.developers.forum.api.response.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 配置响应类
 * 用于返回系统配置信息的响应对象
 *
 * @author Qiangqiang.Bian
 * @create 2020/12/26
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConfigResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 配置ID
     */
    private Long id;

    /**
     * 配置状态
     */
    private String state;

    /**
     * 配置类型
     */
    private String type;

    /**
     * 配置名称
     */
    private String name;

    /**
     * 配置内容
     */
    private String content;

    /**
     * 生效开始时间
     */
    private Date startAt;

    /**
     * 生效结束时间
     */
    private Date endAt;

    /**
     * 创建人ID
     */
    private Long creator;

    /**
     * 创建时间
     */
    private Date createAt;

    /**
     * 更新时间
     */
    private Date updateAt;

}
