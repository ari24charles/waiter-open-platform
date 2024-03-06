package com.ari.waiter.service.impl.inner;

import com.ari.waiter.common.exception.BusinessException;
import com.ari.waiter.common.model.entity.User;
import com.ari.waiter.common.response.StatusCode;
import com.ari.waiter.common.service.InnerUserService;
import com.ari.waiter.mapper.UserMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

/**
 * 内部用户服务实现类
 *
 * @author ari24charles
 */
@DubboService
public class InnerUserServiceImpl implements InnerUserService {

    @Resource
    private UserMapper userMapper;

    @Override
    public User getInvokeUser(String accessKey) {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getAccessKey, accessKey));
        if (user == null) {
            throw new BusinessException(StatusCode.NO_USER);
        }
        return user;
    }
}
