package com.ari.waiter.common.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 使用 id 操作的通用请求体
 *
 * @author ari24charles
 */
@Data
public class IdRequest implements Serializable {

    /**
     * 雪花算法主键
     */
    private Long id;
}
