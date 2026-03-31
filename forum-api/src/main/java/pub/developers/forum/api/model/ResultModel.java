package pub.developers.forum.api.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Data;

/**
 * Generic API response model
 * 
 * @author Qiangqiang.Bian
 * @create 20/7/23
 * @desc
 * @param <T> the type of data contained in the response
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResultModel<T> {
    // HTTP status code, default 200 for success
    private Integer code = 200;
    // Response message, default "success"
    private String message = "success";
    // Indicates whether the operation was successful
    private Boolean success = Boolean.TRUE;
    // The actual data payload
    private T data;
}