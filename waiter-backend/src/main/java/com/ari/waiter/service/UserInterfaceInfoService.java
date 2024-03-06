package com.ari.waiter.service;

import com.ari.waiter.common.model.entity.UserInterfaceInfo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author ari24charles
* @description 针对表【user_interface_info(用户与接口信息的调用关系表)】的数据库操作Service
* @createDate 2024-03-06 14:25:03
*/
public interface UserInterfaceInfoService extends IService<UserInterfaceInfo> {

    /**
     * 调用接口统计
     * @param interfaceInfoId 接口信息 id
     * @param userId 用户 id
     * @return true || false
     */
    boolean invokeCount(long interfaceInfoId, long userId);
}
