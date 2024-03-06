package com.ari.waiter.common.service;

import com.ari.waiter.common.model.entity.InterfaceInfo;

/**
 * 内部接口信息服务
 *
 * @author ari24charles
 */
public interface InnerInterfaceInfoService {

    /**
     * 根据接口地址和请求类型查询接口信息
     *
     * @param uri    接口地址
     * @param method 请求类型
     * @return 接口信息
     */
    InterfaceInfo getInterfaceInfo(String uri, String method);
}
