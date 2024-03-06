package com.ari.waiter.model.dto.user;

import com.ari.waiter.common.request.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 用户查询其他用户信息请求体
 *
 * @author ari24charles
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserQueryRequest extends PageRequest implements Serializable {

    /**
     * 账号
     */
    private String username;

    /**
     * 昵称
     */
    private String nickname;
}
