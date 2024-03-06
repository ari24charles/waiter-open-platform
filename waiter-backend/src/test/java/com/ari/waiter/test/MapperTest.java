package com.ari.waiter.test;

import com.ari.waiter.common.model.vo.InterfaceInfoAnalyseVo;
import com.ari.waiter.mapper.InterfaceInfoMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
public class MapperTest {

    @Resource
    private InterfaceInfoMapper interfaceInfoMapper;

    @Test
    public void test() {
        InterfaceInfoAnalyseVo vo = interfaceInfoMapper.getInterfaceInfoById(1L);
        System.out.println(vo);
    }
}
