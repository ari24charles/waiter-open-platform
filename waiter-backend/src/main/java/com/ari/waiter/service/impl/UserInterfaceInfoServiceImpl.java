package com.ari.waiter.service.impl;

import com.ari.waiter.common.exception.BusinessException;
import com.ari.waiter.common.response.StatusCode;
import com.ari.waiter.service.UserInterfaceInfoService;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ari.waiter.common.model.entity.UserInterfaceInfo;
import com.ari.waiter.mapper.UserInterfaceInfoMapper;
import org.springframework.stereotype.Service;

/**
* @author ari24charles
* @description 针对表【user_interface_info(用户与接口信息的调用关系表)】的数据库操作Service实现
* @createDate 2024-03-06 14:25:03
*/
@Service
public class UserInterfaceInfoServiceImpl extends ServiceImpl<UserInterfaceInfoMapper, UserInterfaceInfo>
    implements UserInterfaceInfoService {

    @Override
    public boolean invokeCount(long interfaceInfoId, long userId) {
        LambdaUpdateWrapper<UserInterfaceInfo> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(UserInterfaceInfo::getInterfaceInfoId, interfaceInfoId);
        updateWrapper.eq(UserInterfaceInfo::getUserId, userId);
        updateWrapper.setSql("left_num = left_num - 1, total_num = total_num + 1");
        return this.update(updateWrapper);
    }
}




