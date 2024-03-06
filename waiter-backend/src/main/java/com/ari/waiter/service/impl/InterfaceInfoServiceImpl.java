package com.ari.waiter.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.ari.waiter.client.WaiterClient;
import com.ari.waiter.common.exception.BusinessException;
import com.ari.waiter.common.model.enums.InterfaceInfoStatusEnum;
import com.ari.waiter.common.model.vo.InterfaceInfoAnalyseVo;
import com.ari.waiter.common.model.vo.InterfaceInfoVo;
import com.ari.waiter.common.request.IdRequest;
import com.ari.waiter.common.response.BaseResponse;
import com.ari.waiter.common.response.PageResponse;
import com.ari.waiter.common.response.ResultUtils;
import com.ari.waiter.common.response.StatusCode;
import com.ari.waiter.common.utils.SqlUtils;
import com.ari.waiter.common.validator.InterfaceInfoValidator;
import com.ari.waiter.common.validator.UserValidator;
import com.ari.waiter.model.dto.interfaceInfo.InterfaceInfoAddRequest;
import com.ari.waiter.model.dto.interfaceInfo.InterfaceInfoQueryAdminRequest;
import com.ari.waiter.model.dto.interfaceInfo.InterfaceInfoQueryRequest;
import com.ari.waiter.model.dto.interfaceInfo.InterfaceInfoUpdateRequest;
import com.ari.waiter.service.InterfaceInfoService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ari.waiter.common.model.entity.InterfaceInfo;
import com.ari.waiter.mapper.InterfaceInfoMapper;
import com.google.gson.Gson;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.ari.waiter.common.constant.CommonConstant.SORT_ORDER_ASC;

/**
* @author ari24charles
* @description 针对表【interface_info(接口信息表)】的数据库操作Service实现
* @createDate 2024-03-06 14:25:03
*/
@Service
public class InterfaceInfoServiceImpl extends ServiceImpl<InterfaceInfoMapper, InterfaceInfo>
    implements InterfaceInfoService {

    @Resource
    private InterfaceInfoMapper interfaceInfoMapper;

    @Resource
    private WaiterClient waiterClient;

    @Override
    public BaseResponse<?> addInterfaceInfo(InterfaceInfoAddRequest interfaceInfoAddRequest, HttpServletRequest request) {
        // 校验
        String name = interfaceInfoAddRequest.getName();
        String description = interfaceInfoAddRequest.getDescription();
        String uri = interfaceInfoAddRequest.getUri();
        String requestParams = interfaceInfoAddRequest.getRequestParams();
        String requestHeader = interfaceInfoAddRequest.getRequestHeader();
        String responseHeader = interfaceInfoAddRequest.getResponseHeader();
        String method = interfaceInfoAddRequest.getMethod();
        InterfaceInfoValidator.validName(name);
        if (StrUtil.isBlank(uri)) {
            throw new BusinessException(StatusCode.PARAMS_ERROR, "接口地址是必要的");
        }
        if (StrUtil.isBlank(method)) {
            throw new BusinessException(StatusCode.PARAMS_ERROR, "请求类型是必要的");
        }
        long count = this.count(new LambdaQueryWrapper<InterfaceInfo>().eq(InterfaceInfo::getName, name));
        if (count > 0) {
            throw new BusinessException(StatusCode.PARAMS_ERROR, "接口名已存在");
        }
        count = this.count(new LambdaQueryWrapper<InterfaceInfo>().eq(InterfaceInfo::getUri, uri));
        if (count > 0) {
            throw new BusinessException(StatusCode.PARAMS_ERROR, "接口地址已存在");
        }
        if (StrUtil.hasBlank(requestParams, requestHeader, responseHeader)) {
            throw new BusinessException(StatusCode.PARAMS_ERROR, "请求和响应参数是必要的");
        }
        // 获取当前用户
        Long loginUserId = UserValidator.validUserLoginState(request);
        if (loginUserId == null || loginUserId <= 0) {
            throw new BusinessException(StatusCode.NOT_LOGIN);
        }
        // 设置接口信息
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        interfaceInfo.setName(name);
        interfaceInfo.setDescription(description);
        interfaceInfo.setUri(uri);
        interfaceInfo.setRequestParams(requestParams);
        interfaceInfo.setRequestHeader(requestHeader);
        interfaceInfo.setResponseHeader(responseHeader);
        interfaceInfo.setMethod(method);
        interfaceInfo.setUserId(loginUserId);
        boolean isSuccess = this.save(interfaceInfo);
        if (isSuccess) {
            throw new BusinessException(StatusCode.OPERATION_ERROR);
        }
        return ResultUtils.success();
    }

    @Override
    public BaseResponse<?> updateInterfaceInfo(InterfaceInfoUpdateRequest interfaceInfoUpdateRequest) {
        Long id = interfaceInfoUpdateRequest.getId();
        String name = interfaceInfoUpdateRequest.getName();
        String description = interfaceInfoUpdateRequest.getDescription();
        String uri = interfaceInfoUpdateRequest.getUri();
        String requestParams = interfaceInfoUpdateRequest.getRequestParams();
        String requestHeader = interfaceInfoUpdateRequest.getRequestHeader();
        String responseHeader = interfaceInfoUpdateRequest.getResponseHeader();
        String method = interfaceInfoUpdateRequest.getMethod(); // todo 校验 method 字段
        Integer status = interfaceInfoUpdateRequest.getStatus();
        if (id == null || id <= 0) {
            throw new BusinessException(StatusCode.PARAMS_ERROR);
        }
        if (StrUtil.isAllBlank(name, description, uri, requestHeader, responseHeader, method)) {
            if (status == null) {
                throw new BusinessException(StatusCode.PARAMS_ERROR, "所有参数都为空");
            }
        }
        if (status != null) {
            InterfaceInfoValidator.validStatus(status);
        }
        if (name != null) {
            InterfaceInfoValidator.validName(name);
            long count = this.count(new LambdaQueryWrapper<InterfaceInfo>().eq(InterfaceInfo::getName, name));
            if (count > 0) {
                throw new BusinessException(StatusCode.PARAMS_ERROR, "接口名已存在");
            }
        }
        if (uri != null) {
            long count = this.count(new LambdaQueryWrapper<InterfaceInfo>().eq(InterfaceInfo::getUri, uri));
            if (count > 0) {
                throw new BusinessException(StatusCode.PARAMS_ERROR, "接口地址已存在");
            }
        }
        boolean isSuccess = this.update(new LambdaUpdateWrapper<InterfaceInfo>()
                .set(name != null, InterfaceInfo::getName, name)
                .set(description != null, InterfaceInfo::getDescription, description)
                .set(uri != null, InterfaceInfo::getUri, uri)
                .set(requestParams != null, InterfaceInfo::getRequestParams, requestHeader)
                .set(requestHeader != null, InterfaceInfo::getRequestHeader, requestHeader)
                .set(responseHeader != null, InterfaceInfo::getResponseHeader, responseHeader)
                .set(method != null, InterfaceInfo::getMethod, method)
                .set(status != null, InterfaceInfo::getStatus, status)
                .eq(InterfaceInfo::getId, id));
        if (!isSuccess) {
            throw new BusinessException(StatusCode.OPERATION_ERROR);
        }
        return ResultUtils.success();
    }

    @Override
    public BaseResponse<?> deleteInterfaceInfo(IdRequest idRequest) {
        Long interfaceId = idRequest.getId();
        if (interfaceId == null || interfaceId <= 0) {
            throw new BusinessException(StatusCode.PARAMS_ERROR);
        }
        boolean isSuccess = this.removeById(interfaceId);
        if (!isSuccess) {
            throw new BusinessException(StatusCode.OPERATION_ERROR, "接口已删除");
        }
        return ResultUtils.success();
    }

    @Override
    public BaseResponse<PageResponse<InterfaceInfoVo>> searchInterfaceInfo(InterfaceInfoQueryRequest interfaceInfoQueryRequest) {
        // 校验
        String name = interfaceInfoQueryRequest.getName();
        String description = interfaceInfoQueryRequest.getDescription();
        String method = interfaceInfoQueryRequest.getMethod();
        Integer currentPage = interfaceInfoQueryRequest.getCurrent();
        Integer pageSize = interfaceInfoQueryRequest.getPageSize();
        String sortField = interfaceInfoQueryRequest.getSortField();
        String sortOrder = interfaceInfoQueryRequest.getSortOrder();
        // 动态查询
        QueryWrapper<InterfaceInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(name != null, "name", name)
                .or(description != null).like(description != null, "description", description)
                .eq(method != null, "method", method)
                .orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(SORT_ORDER_ASC), sortField);
        // 分页查询
        IPage<InterfaceInfo> page = new Page<>(currentPage, pageSize);
        IPage<InterfaceInfo> interfaceInfoPage = this.page(page, queryWrapper);
        // 组装返回结果
        PageResponse<InterfaceInfoVo> pageResponse = new PageResponse<>();
        pageResponse.setCurrent(interfaceInfoPage.getCurrent());
        pageResponse.setSize(interfaceInfoPage.getSize());
        pageResponse.setTotal(interfaceInfoPage.getTotal());
        List<InterfaceInfo> interfaceInfoList = interfaceInfoPage.getRecords();
        List<InterfaceInfoVo> interfaceInfoVoList = new ArrayList<>();
        for (InterfaceInfo interfaceInfo : interfaceInfoList) {
            InterfaceInfoVo interfaceInfoVo = new InterfaceInfoVo();
            BeanUtil.copyProperties(interfaceInfo, interfaceInfoVo);
            interfaceInfoVoList.add(interfaceInfoVo);
        }
        pageResponse.setRecords(interfaceInfoVoList);
        return ResultUtils.success(pageResponse);
    }

    @Override
    public BaseResponse<PageResponse<InterfaceInfo>> listInterfaceInfo(InterfaceInfoQueryAdminRequest interfaceInfoQueryAdminRequest) {
        // 校验
        String name = interfaceInfoQueryAdminRequest.getName();
        String description = interfaceInfoQueryAdminRequest.getDescription();
        String uri = interfaceInfoQueryAdminRequest.getUri();
        String method = interfaceInfoQueryAdminRequest.getMethod();
        Long userId = interfaceInfoQueryAdminRequest.getUserId();
        Integer status = interfaceInfoQueryAdminRequest.getStatus();
        Integer currentPage = interfaceInfoQueryAdminRequest.getCurrent();
        Integer pageSize = interfaceInfoQueryAdminRequest.getPageSize();
        String sortField = interfaceInfoQueryAdminRequest.getSortField();
        String sortOrder = interfaceInfoQueryAdminRequest.getSortOrder();
        // 动态查询
        QueryWrapper<InterfaceInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(name != null, "name", name)
                .like(description != null, "description", description)
                .like(uri != null, "uri", uri)
                .eq(method != null, "method", method)
                .eq(userId != null, "user_id", userId)
                .eq(status != null, "status", status)
                .orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(SORT_ORDER_ASC), sortField);
        // 分页查询
        IPage<InterfaceInfo> page = new Page<>(currentPage, pageSize);
        IPage<InterfaceInfo> interfaceInfoPage = this.page(page, queryWrapper);
        // 组装返回结果
        PageResponse<InterfaceInfo> pageResponse = new PageResponse<>();
        pageResponse.setCurrent(interfaceInfoPage.getCurrent());
        pageResponse.setSize(interfaceInfoPage.getSize());
        pageResponse.setTotal(interfaceInfoPage.getTotal());
        pageResponse.setRecords(interfaceInfoPage.getRecords());
        return ResultUtils.success(pageResponse);
    }

    @Override
    public BaseResponse<InterfaceInfoAnalyseVo> getInterfaceInfoById(IdRequest idRequest) {
        Long interfaceId = idRequest.getId();
        if (interfaceId == null || interfaceId <= 0) {
            throw new BusinessException(StatusCode.PARAMS_ERROR);
        }
        InterfaceInfoAnalyseVo interfaceInfoAnalyseVo = interfaceInfoMapper.getInterfaceInfoById(interfaceId);
        return ResultUtils.success(interfaceInfoAnalyseVo);
    }

    @Override
    public BaseResponse<?> switchInterfaceStatus(IdRequest idRequest) {
        Long interfaceId = idRequest.getId();
        if (interfaceId == null || interfaceId <= 0) {
            throw new BusinessException(StatusCode.PARAMS_ERROR, "参数错误");
        }
        // 1. 校验接口是否存在
        InterfaceInfo interfaceInfo = this.getById(interfaceId);
        if (interfaceInfo == null) {
            throw new BusinessException(StatusCode.PARAMS_ERROR, "接口不存在");
        }
        InterfaceInfo updateInfo = new InterfaceInfo();
        updateInfo.setId(interfaceId);
        if (interfaceInfo.getStatus().equals(InterfaceInfoStatusEnum.OFFLINE.getValue())) {
            String method = interfaceInfo.getMethod();
            String requestParams = interfaceInfo.getRequestParams();
            String uri = interfaceInfo.getUri();
            String res = null;
            if (method.equalsIgnoreCase("post")) {
                res = waiterClient.post(uri, requestParams);
            } else if (method.equalsIgnoreCase("get")) {
                res = waiterClient.get(uri, null);
            } else {
                throw new BusinessException(StatusCode.PARAMS_ERROR, "请求类型错误");
            }
            if (res == null) {
                throw new BusinessException(StatusCode.SYSTEM_ERROR, "接口不可用");
            }
            System.out.println("res = " + res);
            updateInfo.setStatus(InterfaceInfoStatusEnum.ONLINE.getValue());
        } else {
            if (interfaceInfo.getStatus().equals(InterfaceInfoStatusEnum.ONLINE.getValue())) {
                updateInfo.setStatus(InterfaceInfoStatusEnum.OFFLINE.getValue());
            } else {
                throw new BusinessException(StatusCode.OPERATION_ERROR, "接口状态异常");
            }
        }
        boolean isSuccess = this.updateById(updateInfo);
        if (!isSuccess) {
            throw new BusinessException(StatusCode.OPERATION_ERROR);
        }
        return ResultUtils.success();
    }
}