package com.ari.waiter.service;

import com.ari.waiter.common.model.entity.InterfaceInfo;
import com.ari.waiter.common.model.vo.InterfaceInfoAnalyseVo;
import com.ari.waiter.common.model.vo.InterfaceInfoVo;
import com.ari.waiter.common.request.IdRequest;
import com.ari.waiter.common.response.BaseResponse;
import com.ari.waiter.common.response.PageResponse;
import com.ari.waiter.model.dto.interfaceInfo.InterfaceInfoAddRequest;
import com.ari.waiter.model.dto.interfaceInfo.InterfaceInfoQueryAdminRequest;
import com.ari.waiter.model.dto.interfaceInfo.InterfaceInfoQueryRequest;
import com.ari.waiter.model.dto.interfaceInfo.InterfaceInfoUpdateRequest;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;

/**
* @author ari24charles
* @description 针对表【interface_info(接口信息表)】的数据库操作Service
* @createDate 2024-03-06 14:25:03
*/
public interface InterfaceInfoService extends IService<InterfaceInfo> {

    /**
     * 管理员添加接口信息
     *
     * @param interfaceInfoAddRequest 添加接口信息请求体
     * @return null
     */
    BaseResponse<?> addInterfaceInfo(InterfaceInfoAddRequest interfaceInfoAddRequest, HttpServletRequest request);

    /**
     * 管理员更新接口信息
     * @param interfaceInfoUpdateRequest 更新接口信息请求体
     * @return null
     */
    BaseResponse<?> updateInterfaceInfo(InterfaceInfoUpdateRequest interfaceInfoUpdateRequest);

    /**
     * 管理员删除接口信息
     * @param idRequest 包含接口 id 的请求体
     * @return null
     */
    BaseResponse<?> deleteInterfaceInfo(IdRequest idRequest);

    /**
     * 查询接口信息
     *
     * @param interfaceInfoQueryRequest 接口信息查询请求体
     * @return 接口视图列表
     */
    BaseResponse<PageResponse<InterfaceInfoVo>> searchInterfaceInfo(InterfaceInfoQueryRequest interfaceInfoQueryRequest);

    /**
     * 查询接口信息 [管理员]
     *
     * @param interfaceInfoQueryAdminRequest 接口信息查询请求体
     * @return 接口信息列表
     */
    BaseResponse<PageResponse<InterfaceInfo>> listInterfaceInfo(InterfaceInfoQueryAdminRequest interfaceInfoQueryAdminRequest);

    /**
     * 查看接口的详细信息视图 (包括调用次数统计)
     *
     * @param idRequest 包含接口 id 的请求体
     * @return 接口信息统计视图
     */
    BaseResponse<InterfaceInfoAnalyseVo> getInterfaceInfoById(IdRequest idRequest);

    /**
     * 关闭 / 发布接口
     *
     * @param idRequest 包含接口 id 的请求
     * @return null
     */
    BaseResponse<?> switchInterfaceStatus(IdRequest idRequest);
}
