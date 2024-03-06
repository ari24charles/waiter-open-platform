package com.ari.waiter.common.model.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class InterfaceInfoVo implements Serializable {

    /**
     * 雪花算法主键
     */
    private Long id;

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
     * 请求类型: POST / GET
     */
    private String method;
}
