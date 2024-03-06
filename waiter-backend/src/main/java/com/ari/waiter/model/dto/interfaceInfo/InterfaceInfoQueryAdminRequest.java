package com.ari.waiter.model.dto.interfaceInfo;

import com.ari.waiter.common.request.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 接口分页查询请求体 [管理员]
 *
 * @author ari24charles
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class InterfaceInfoQueryAdminRequest extends PageRequest implements Serializable {

    /**
     * 接口名
     */
    private String name;

    /**
     * 接口描述
     */
    private String description;

    /**
     * 接口地址
     */
    private String uri;

    /**
     * 请求类型
     */
    private String method;

    /**
     * 创建人
     */
    private Long userId;

    /**
     * 接口状态: 0 -> 关闭, 1 -> 开启
     */
    private Integer status;
}
