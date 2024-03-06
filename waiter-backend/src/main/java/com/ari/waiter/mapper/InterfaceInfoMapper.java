package com.ari.waiter.mapper;

import com.ari.waiter.common.model.entity.InterfaceInfo;
import com.ari.waiter.common.model.vo.InterfaceInfoAnalyseVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* @author ari24charles
* @description 针对表【interface_info(接口信息表)】的数据库操作Mapper
* @createDate 2024-03-06 14:25:03
* @Entity generator.domain.InterfaceInfo
*/
public interface InterfaceInfoMapper extends BaseMapper<InterfaceInfo> {

    InterfaceInfoAnalyseVo getInterfaceInfoById(Long id);

}




