package com.ari.waiter.common.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * 通用返回类
 *
 * @param <T> 返回数据的类型
 * @author ari24charles
 */
@Data
@AllArgsConstructor
public class BaseResponse<T> implements Serializable {

    private Integer code; // 状态码

    private String msg; // 状态码简讯

    private T data; // 返回数据

    private String description; // 状态码详情

    public BaseResponse(StatusCode statusCode) {
        this(statusCode.getCode(), statusCode.getMsg(), null, null);
    }

    public BaseResponse(StatusCode statusCode, T data) {
        this(statusCode.getCode(), statusCode.getMsg(), data, null);
    }

    public BaseResponse(StatusCode statusCode, String description) {
        this(statusCode.getCode(), statusCode.getMsg(), null, description);
    }

    public BaseResponse(Integer code, String msg, String description) {
        this(code, msg, null, description);
    }
}
