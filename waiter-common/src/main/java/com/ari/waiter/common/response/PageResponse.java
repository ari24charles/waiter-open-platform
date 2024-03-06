package com.ari.waiter.common.response;

import lombok.Data;

import java.util.List;

/**
 * 做分页查询时，包含分页信息和数据的封装类
 *
 * @param <T> 返回数据的类型
 * @author ari24charles
 */
@Data
public class PageResponse<T> {

    private Long current; // 当前页号

    private Long size; // 页面大小

    private Long total; // 总记录数

    private List<T> records; // 查询记录
}
