package com.ari.waiter.model.dto.userInterfaceInfo;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户开通接口请求体
 *
 * @author ari24charles
 */
@Data
public class UserInterfaceInfoAddRequest implements Serializable {

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
}
