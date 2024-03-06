package com.ari.waiter.common.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 用户性别枚举类
 *
 * @author ari24charles
 */
@Getter
@AllArgsConstructor
public enum UserGenderEnum {

    UNKNOWN(0, "unknown"),
    MAlE(1, "male"),
    FEMALE(2, "female");

    private final Integer value; // 数据库中存储的值, 性别代码

    private final String text; // 值对应的含义, 性别
}
