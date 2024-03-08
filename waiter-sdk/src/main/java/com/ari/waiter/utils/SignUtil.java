package com.ari.waiter.utils;

import cn.hutool.crypto.digest.DigestAlgorithm;
import cn.hutool.crypto.digest.Digester;

/**
 * 签名工具类
 *
 * @author ari24charles
 */
public class SignUtil {

    /**
     * 生成签名
     *
     * @param body      序列化后的请求体
     * @param secretKey 签名密钥
     * @return 签名
     */
    public static String genSign(String body, String secretKey) {
        Digester digester = new Digester(DigestAlgorithm.SHA256);
        String content = body + "." + secretKey;
        return digester.digestHex(content);
    }
}
