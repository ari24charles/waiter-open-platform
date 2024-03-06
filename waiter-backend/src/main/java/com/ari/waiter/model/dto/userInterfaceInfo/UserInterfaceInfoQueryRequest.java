package com.ari.waiter.model.dto.userInterfaceInfo;

import com.ari.waiter.common.request.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserInterfaceInfoQueryRequest extends PageRequest implements Serializable {

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
     * 接口状态: 0 -> 正常, 1 -> 禁用
     */
    private Integer status;
}
