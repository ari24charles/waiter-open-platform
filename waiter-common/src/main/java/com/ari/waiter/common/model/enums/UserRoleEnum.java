package com.ari.waiter.common.model.enums;

import cn.hutool.core.util.StrUtil;
import com.ari.waiter.common.exception.BusinessException;
import com.ari.waiter.common.response.StatusCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 用户角色枚举类
 *
 * @author ari24charles
 */
@Getter
@AllArgsConstructor
public enum UserRoleEnum {

    SUPER_ADMIN(0, "super_admin"),
    ADMIN(1, "admin"),
    USER(2, "user");

    private final Integer value; // 数据库中存储的值, 角色代码

    private final String text; // 值对应的含义, 角色名

    /**
     * 根据角色名获取角色代码
     *
     * @param text 角色名
     * @return 角色代码
     */
    public static Integer getValueByText(String text) {
        if (StrUtil.isBlank(text)) { // 传入角色名为空
            throw new BusinessException(StatusCode.SYSTEM_ERROR);
        }
        for (UserRoleEnum anEnum : UserRoleEnum.values()) {
            if (anEnum.getText().equals(text)) {
                return anEnum.getValue();
            }
        }
        throw new BusinessException(StatusCode.SYSTEM_ERROR);
    }
}
