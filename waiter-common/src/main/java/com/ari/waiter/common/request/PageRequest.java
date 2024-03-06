package com.ari.waiter.common.request;

import lombok.Data;

import static com.ari.waiter.common.constant.CommonConstant.SORT_ORDER_ASC;

/**
 * 分页信息请求体 (用于被继承，作为分页查询请求体的必要部分)
 *
 * @author ari24charles
 */
@Data
public class PageRequest {

    private Integer current = 1; // 当前页号 (默认第一页)

    private Integer pageSize = 10; // 页面大小 (默认每页十条)

    private String sortField; // 排序字段

    private String sortOrder = SORT_ORDER_ASC; // 排序方式 (默认 ASC)
}
