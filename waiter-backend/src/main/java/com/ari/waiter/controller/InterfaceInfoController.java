package com.ari.waiter.controller;

import com.ari.waiter.common.annotation.AuthCheck;
import com.ari.waiter.common.exception.BusinessException;
import com.ari.waiter.common.model.vo.InterfaceInfoAnalyseVo;
import com.ari.waiter.common.request.IdRequest;
import com.ari.waiter.common.response.BaseResponse;
import com.ari.waiter.common.response.StatusCode;
import com.ari.waiter.model.dto.interfaceInfo.InterfaceInfoAddRequest;
import com.ari.waiter.model.dto.interfaceInfo.InterfaceInfoQueryAdminRequest;
import com.ari.waiter.model.dto.interfaceInfo.InterfaceInfoQueryRequest;
import com.ari.waiter.model.dto.interfaceInfo.InterfaceInfoUpdateRequest;
import com.ari.waiter.service.InterfaceInfoService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import static com.ari.waiter.common.constant.UserConstant.ADMIN;
import static com.ari.waiter.common.constant.UserConstant.SUPER_ADMIN;

@RestController
@RequestMapping("/interfaceInfo")
public class InterfaceInfoController {

    @Resource
    private InterfaceInfoService interfaceInfoService;

    /**
     * 管理员添加接口信息
     *
     * @param interfaceInfoAddRequest 添加接口信息请求体
     * @param request                 请求
     * @return null
     */
    @PostMapping("/add")
    @AuthCheck(mustRole = {SUPER_ADMIN, ADMIN})
    public BaseResponse<?> addInterfaceInfo(@RequestBody InterfaceInfoAddRequest interfaceInfoAddRequest,
                                            HttpServletRequest request) {
        if (interfaceInfoAddRequest == null) {
            throw new BusinessException(StatusCode.PARAMS_ERROR, "请求为空");
        }
        return interfaceInfoService.addInterfaceInfo(interfaceInfoAddRequest, request);
    }

    /**
     * 管理员更新接口信息
     *
     * @param interfaceInfoUpdateRequest 更新接口信息请求体
     * @return null
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = {SUPER_ADMIN, ADMIN})
    public BaseResponse<?> updateInterfaceInfo(@RequestBody InterfaceInfoUpdateRequest interfaceInfoUpdateRequest) {
        if (interfaceInfoUpdateRequest == null) {
            throw new BusinessException(StatusCode.PARAMS_ERROR, "请求为空");
        }
        return interfaceInfoService.updateInterfaceInfo(interfaceInfoUpdateRequest);
    }

    /**
     * 管理员删除接口信息
     *
     * @param idRequest 包含接口 id 的请求体
     * @return null
     */
    @PostMapping("/delete")
    @AuthCheck(mustRole = {SUPER_ADMIN, ADMIN})
    public BaseResponse<?> deleteInterfaceInfo(@RequestBody IdRequest idRequest) {
        if (idRequest == null) {
            throw new BusinessException(StatusCode.PARAMS_ERROR, "请求为空");
        }
        return interfaceInfoService.deleteInterfaceInfo(idRequest);
    }

    /**
     * 查询接口信息
     *
     * @param interfaceInfoQueryRequest 接口信息查询请求体
     * @return 接口视图列表
     */
    @PostMapping("/search")
    public BaseResponse<?> searchInterfaceInfo(@RequestBody InterfaceInfoQueryRequest interfaceInfoQueryRequest) {
        if (interfaceInfoQueryRequest == null) {
            throw new BusinessException(StatusCode.PARAMS_ERROR, "请求为空");
        }
        return interfaceInfoService.searchInterfaceInfo(interfaceInfoQueryRequest);
    }

    /**
     * 查询接口信息 [管理员]
     *
     * @param interfaceInfoQueryAdminRequest 接口信息查询请求体
     * @return 接口信息列表
     */
    @PostMapping("/list")
    @AuthCheck(mustRole = {SUPER_ADMIN, ADMIN})
    public BaseResponse<?> listInterfaceInfo(@RequestBody InterfaceInfoQueryAdminRequest interfaceInfoQueryAdminRequest) {
        if (interfaceInfoQueryAdminRequest == null) {
            throw new BusinessException(StatusCode.PARAMS_ERROR, "请求为空");
        }
        return interfaceInfoService.listInterfaceInfo(interfaceInfoQueryAdminRequest);
    }

    /**
     * 查看接口的详细信息视图 (包括调用次数统计)
     *
     * @param idRequest 包含接口 id 的请求体
     * @return 接口信息统计视图
     */
    @PostMapping("/info")
    public BaseResponse<InterfaceInfoAnalyseVo> getInterfaceInfoById(@RequestBody IdRequest idRequest) {
        if (idRequest == null) {
            throw new BusinessException(StatusCode.PARAMS_ERROR, "请求为空");
        }
        return interfaceInfoService.getInterfaceInfoById(idRequest);
    }

    /**
     * 下线 / 发布接口
     *
     * @param idRequest 包含接口 id 的请求
     * @return null
     */
    @PostMapping("/status")
    @AuthCheck(mustRole = {SUPER_ADMIN, ADMIN})
    public BaseResponse<?> switchInterfaceStatus(@RequestBody IdRequest idRequest) {
        if (idRequest == null) {
            throw new BusinessException(StatusCode.PARAMS_ERROR, "请求为空");
        }
        return interfaceInfoService.switchInterfaceStatus(idRequest);
    }
}
