package pub.developers.forum.api.response.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * Configuration Response
 * Response object for returning system configuration information
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
     * Configuration ID
     */
    private Long id;

    /**
     * Configuration state
     */
    private String state;

    /**
     * Configuration type
     */
    private String type;

    /**
     * Configuration name
     */
    private String name;

    /**
     * Configuration content
     */
    private String content;

    /**
     * Effective start time
     */
    private Date startAt;

    /**
     * Effective end time
     */
    private Date endAt;

    /**
     * Creator user ID
     */
    private Long creator;

    /**
     * Creation time
     */
    private Date createAt;

    /**
     * Update time
     */
    private Date updateAt;

}
