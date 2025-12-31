package pub.developers.forum.facade.support;

import pub.developers.forum.api.model.ResultModel;
import pub.developers.forum.common.enums.ErrorCodeEn;

public class ResultModelUtil {

    private ResultModelUtil() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static ResultModel success() {
        return ResultModel.builder().build();
    }

    public static <T> ResultModel<T> success(T data) {
        return ResultModel.<T>builder()
                .data(data)
                .build();
    }

    public static ResultModel fail(ErrorCodeEn errorCode) {
        return ResultModel.builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .success(Boolean.FALSE)
                .build();
    }

    public static ResultModel fail(Integer code, String message) {
        return ResultModel.builder()
                .code(code)
                .message(message)
                .success(Boolean.FALSE)
                .build();
    }

}