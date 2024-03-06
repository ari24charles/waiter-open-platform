package com.ari.waiter.model.dto.interfaceInfo;

import com.ari.waiter.common.request.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 接口分页查询请求体
 *
 * @author ari24charles
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class InterfaceInfoQueryRequest extends PageRequest implements Serializable {

    /**
     * 接口名
     */
    private String name;

    /**
     * 接口描述
     */
    private String description;

    /**
     * 请求类型
     */
    private String method;
}
