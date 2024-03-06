package com.ari.waiter.model.dto.interfaceInfo;

import lombok.Data;

import java.io.Serializable;

@Data
public class InterfaceInfoInvokeRequest implements Serializable {

    /**
     * 雪花算法主键
     */
    private Long id;

    /**
     * 请求参数
     */
    private String requestParams;
}
