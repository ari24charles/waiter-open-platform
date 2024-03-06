package com.ari.waiter.model.dto.interfaceInfo;

import lombok.Data;

import java.io.Serializable;

/**
 * 新增接口请求类
 *
 * @author ari24charles
 */
@Data
public class InterfaceInfoAddRequest implements Serializable {

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
     * 请求参数
     */
    private String requestParams;

    /**
     * 请求头
     */
    private String requestHeader;

    /**
     * 响应头
     */
    private String responseHeader;

    /**
     * 请求类型
     */
    private String method;
}
