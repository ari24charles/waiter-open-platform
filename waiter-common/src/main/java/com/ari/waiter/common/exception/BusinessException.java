package com.ari.waiter.common.exception;

import com.ari.waiter.common.response.StatusCode;
import lombok.Getter;

/**
 * 自定义业务异常
 *
 * @author ari24charles
 */
@Getter
public class BusinessException extends RuntimeException {

    private final Integer code; // 错误码

    private final String description; // 错误描述

    public BusinessException(Integer code, String msg, String description) {
        super(msg);
        this.code = code;
        this.description = description;
    }

    public BusinessException(StatusCode statusCode) {
        this(statusCode.getCode(), statusCode.getMsg(), null);
    }

    public BusinessException(StatusCode statusCode, String description) {
        this(statusCode.getCode(), statusCode.getMsg(), description);
    }
}
