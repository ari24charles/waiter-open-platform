package com.ari.waiter.service.impl.inner;

import com.ari.waiter.common.exception.BusinessException;
import com.ari.waiter.common.model.entity.UserInterfaceInfo;
import com.ari.waiter.common.response.StatusCode;
import com.ari.waiter.common.service.InnerUserInterfaceInfoService;
import com.ari.waiter.mapper.UserInterfaceInfoMapper;
import com.ari.waiter.service.UserInterfaceInfoService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

/**
 * 内部用户与接口信息调用关系服务的实现类
 *
 * @author ari24charles
 */
@DubboService
public class InnerUserInterfaceInfoServiceImpl implements InnerUserInterfaceInfoService {

    @Resource
    private UserInterfaceInfoMapper userInterfaceInfoMapper;

    @Resource
    private UserInterfaceInfoService userInterfaceInfoService;

    @Override
    public boolean invokeCount(Long interfaceInfoId, Long userId) {
        if (interfaceInfoId == null || userId == null || interfaceInfoId <= 0 || userId <= 0) {
            return false;
        }
        return userInterfaceInfoService.invokeCount(interfaceInfoId, userId);
    }

    @Override
    public boolean judgeLeftNum(Long interfaceInfoId, Long userId) {
        if (interfaceInfoId == null || userId == null || interfaceInfoId <= 0 || userId <= 0) {
            return false;
        }
        UserInterfaceInfo userInterfaceInfo = userInterfaceInfoMapper.selectOne(new LambdaQueryWrapper<UserInterfaceInfo>()
                .eq(UserInterfaceInfo::getInterfaceInfoId, interfaceInfoId)
                .eq(UserInterfaceInfo::getUserId, userId));
        if (userInterfaceInfo == null) {
            throw new BusinessException(StatusCode.PARAMS_ERROR, "用户没有开通这个接口");
        }
        if (userInterfaceInfo.getLeftNum() <= 0) {
            return false; // 调用次数耗尽
        } else {
            return true;
        }
    }
}
