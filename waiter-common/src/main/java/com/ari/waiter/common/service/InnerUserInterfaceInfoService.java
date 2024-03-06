package com.ari.waiter.common.service;

/**
 * 内部用户与接口信息的调用关系服务
 *
 * @author ari24charles
 */
public interface InnerUserInterfaceInfoService {

    /**
     * 接口调用统计
     *
     * @param interfaceInfoId 接口信息 id
     * @param userId          用户 id
     * @return true || false
     */
    boolean invokeCount(Long interfaceInfoId, Long userId);

    /**
     * 判断是否还有调用次数
     * @param interfaceInfoId 接口信息 id
     * @param userId 用户 id
     * @return true || false
     */
    boolean judgeLeftNum(Long interfaceInfoId, Long userId);
}
