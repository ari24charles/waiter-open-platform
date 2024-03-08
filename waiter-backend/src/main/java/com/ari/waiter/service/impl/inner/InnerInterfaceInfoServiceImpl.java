package com.ari.waiter.service.impl.inner;

import com.ari.waiter.common.exception.BusinessException;
import com.ari.waiter.common.model.entity.InterfaceInfo;
import com.ari.waiter.common.response.StatusCode;
import com.ari.waiter.common.service.InnerInterfaceInfoService;
import com.ari.waiter.mapper.InterfaceInfoMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

/**
 * 内部接口信息服务实现类
 *
 * @author ari24charles
 */
@DubboService
public class InnerInterfaceInfoServiceImpl implements InnerInterfaceInfoService {

    @Resource
    private InterfaceInfoMapper interfaceInfoMapper;

    /**
     * 根据 URI 和请求类型查询唯一的接口信息
     *
     * @param uri    请求地址
     * @param method 请求类型
     * @return 接口信息
     */
    @Override
    public InterfaceInfo getInterfaceInfo(String uri, String method) {
        InterfaceInfo interfaceInfo = interfaceInfoMapper.selectOne(new LambdaQueryWrapper<InterfaceInfo>()
                .eq(InterfaceInfo::getUri, uri)
                .eq(InterfaceInfo::getMethod, method));
        if (interfaceInfo == null) {
            throw new BusinessException(StatusCode.NOT_FOUND);
        }
        return interfaceInfo;
    }
}
