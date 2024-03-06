package com.ari.waiter.model.dto.user;

import com.ari.waiter.common.request.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 管理员查询用户信息请求体
 *
 * @author ari24charles
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserQueryAdminRequest extends PageRequest implements Serializable {

    /**
     * 账号
     */
    private String username;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 性别: 0 -> 保密, 1 -> 男, 2 -> 女
     */
    private Integer gender;

    /**
     * 用户状态: 0 -> 正常, 1 -> 封禁
     */
    private Integer status;

    /**
     * 用户角色: 0 -> 超级管理员, 1 -> 管理员, 2 -> 普通用户
     */
    private Integer role;
}
