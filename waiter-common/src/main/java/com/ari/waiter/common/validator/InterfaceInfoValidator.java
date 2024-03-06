package com.ari.waiter.common.validator;

import cn.hutool.core.util.StrUtil;
import com.ari.waiter.common.exception.BusinessException;
import com.ari.waiter.common.response.StatusCode;

/**
 * 接口信息实体校验类
 *
 * @author ari24charles
 */
public class InterfaceInfoValidator {

    /**
     * 校验接口名字段
     *
     * @param name 接口名
     */
    public static void validName(String name) {
        if (StrUtil.isBlank(name)) {
            throw new BusinessException(StatusCode.PARAMS_ERROR, "接口名为空");
        }
        if (name.length() > 50) {
            throw new BusinessException(StatusCode.PARAMS_ERROR, "接口名过长");
        }
    }

    /**
     * 校验接口状态字段
     *
     * @param status 接口状态
     */
    public static void validStatus(Integer status) {
        if (status != 0 && status != 1) {
            throw new BusinessException(StatusCode.PARAMS_ERROR, "状态异常");
        }
    }
}
