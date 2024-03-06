package com.ari.waiter.common.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 接口状态枚举类
 *
 * @author ari24charles
 */
@Getter
@AllArgsConstructor
public enum InterfaceInfoStatusEnum {

    OFFLINE(0, "close"),
    ONLINE(1, "normal");

    private final Integer value; // 数据库中存储的值, 状态代码

    private final String text; // 值对应的含义, 接口状态
}
