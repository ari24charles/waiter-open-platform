package com.ari.waiter.test;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.digest.DigestUtil;
import org.junit.jupiter.api.Test;

import static com.ari.waiter.common.constant.CommonConstant.WAITER_SALT;

public class UtilsTest {

    @Test
    public void testGen() {
        String accessKey = DigestUtil.md5Hex(WAITER_SALT + "ari24charles" + RandomUtil.randomNumbers(4));
        String secretKey = DigestUtil.md5Hex(WAITER_SALT + "ari24charles" + RandomUtil.randomNumbers(8));
        String encryptPassword = DigestUtil.md5Hex(WAITER_SALT + "12345678");
        System.out.println("accessKey = " + accessKey);
        System.out.println("secretKey = " + secretKey);
        System.out.println("encryptPassword = " + encryptPassword);
    }
}
