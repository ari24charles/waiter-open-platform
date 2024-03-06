package com.ari.waiter.common.service;

import com.ari.waiter.common.model.entity.User;

/**
 * 内部用户服务
 *
 * @author ari24charles
 */
public interface InnerUserService {

    /**
     * 根据密钥查询用户
     *
     * @param accessKey 密钥
     * @return 用户信息
     */
    User getInvokeUser(String accessKey);
}
