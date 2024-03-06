package com.ari.waiter.test;

import cn.hutool.crypto.digest.DigestUtil;
import org.junit.jupiter.api.Test;

public class ServiceTest {

    @Test
    public void test(){
        System.out.println(DigestUtil.md5Hex("12345678"));
    }
}
