package com.ari.waiter.model.dto.userInterfaceInfo;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserInterfaceInfoUpdateRequest implements Serializable {

    /**
     * 雪花算法主键
     */
    private Long id;

    /**
     * 总调用次数
     */
    private Integer totalNum;

    /**
     * 剩余调用次数
     */
    private Integer leftNum;

    /**
     * 接口状态: 0 -> 正常, 1 -> 禁用
     */
    private Integer status;
}
