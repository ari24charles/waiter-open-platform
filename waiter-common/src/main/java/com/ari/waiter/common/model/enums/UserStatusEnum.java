package com.ari.waiter.common.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 用户状态枚举类
 *
 * @author ari24charles
 */
@Getter
@AllArgsConstructor
public enum UserStatusEnum {

    NORMAL(0, "normal"),
    BANNED(1, "banned");

    private final Integer value; // 数据库中存储的值, 状态代码

    private final String text; // 值对应的含义, 状态描述
}
