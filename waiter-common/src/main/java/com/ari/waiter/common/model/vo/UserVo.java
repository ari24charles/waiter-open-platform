package com.ari.waiter.common.model.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserVo implements Serializable {

    /**
     * 雪花算法主键
     */
    private Long id;

    /**
     * 账号
     */
    private String username;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 电话
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 头像地址
     */
    private String avatarUrl;

    /**
     * 性别: 0 -> 保密, 1 -> 男, 2 -> 女
     */
    private Integer gender;

    /**
     * 访问密钥
     */
    private String accessKey;

    /**
     * 密钥
     */
    private String secretKey;
}
