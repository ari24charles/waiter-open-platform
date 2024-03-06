package com.ari.waiter.common.model.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserInterfaceInfoVo implements Serializable {

    /**
     * 雪花算法主键
     */
    private Long id;

    /**
     * 用户 id
     */
    private Long userId;

    /**
     * 接口信息 id
     */
    private Long interfaceInfoId;

    /**
     * 总调用次数
     */
    private Integer totalNum;

    /**
     * 剩余调用次数
     */
    private Integer leftNum;

    /**
     * 用户是否可以调用该接口: 0 -> 正常, 1 -> 禁用
     */
    private Integer status;
}
