package com.ari.waiter.client;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONUtil;
import com.ari.waiter.utils.SignUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * 调用 Waiter! 开放平台所提供接口的客户端
 *
 * @author ari24charles
 */
public class WaiterClient {

    private static final String GATEWAY_ADDR = "http://localhost:8280"; // 网关地址

    private final String accessKey; // 密钥

    private final String secretKey; // 签名密钥

    public WaiterClient(String accessKey, String secretKey) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
    }

    /**
     * 生成请求头参数
     *
     * @param body 请求体 (序列化后的请求体对象 / 请求参数)
     * @return 请求头参数
     */
    public Map<String, String> getHeader(String body) {
        Map<String, String> map = new HashMap<>();
        map.put("accessKey", accessKey); // 密钥
        map.put("nonce", RandomUtil.randomNumbers(8)); // 8位随机数
        map.put("body", body); // 用于验证签名
        map.put("timestamp", String.valueOf(System.currentTimeMillis() / 1000)); // 当前时间戳(秒)
        map.put("sign", SignUtil.genSign(body, secretKey)); // 签名
        return map;
    }

    /**
     * 发送 GET 请求到接口
     *
     * @param uri        接口 URI /api/user/get
     * @param requestMap 参数列表 key=value
     * @return 响应体
     */
    public String get(String uri, Map<String, String> requestMap) {
        String json = JSONUtil.toJsonStr(requestMap);
        StringBuilder stringBuilder = concatParams(uri, requestMap);
        String url = stringBuilder.toString();
        HttpResponse response = HttpRequest.get(url)
                .addHeaders(getHeader(json))
                .execute();
        return response.body();
    }

    /**
     * 将参数列表拼接到 GET 请求的 URL
     *
     * @param uri        接口 URI /api/user/get
     * @param requestMap 参数列表 key=value
     * @return 请求 URL
     */
    private static StringBuilder concatParams(String uri, Map<String, String> requestMap) {
        StringBuilder stringBuilder = new StringBuilder(GATEWAY_ADDR + uri);
        if (!requestMap.isEmpty()) {
            stringBuilder.append("?");
        }
        int index = 0;
        for (Map.Entry<String, String> entry : requestMap.entrySet()) {
            if (index != 0) {
                stringBuilder.append("&");
            }
            stringBuilder.append(entry.getKey());
            stringBuilder.append("=");
            stringBuilder.append(entry.getValue());
            ++index;
        }
        return stringBuilder;
    }

    /**
     * 发送 POST 请求到接口
     *
     * @param uri           接口 URI /api/user/get
     * @param requestParams 请求体 (序列化后的请求体对象)
     * @return 响应体
     */
    public String post(String uri, String requestParams) {
        HttpResponse response = HttpRequest.post(GATEWAY_ADDR + uri)
                .addHeaders(getHeader(requestParams))
                .body(requestParams)
                .execute();
        return response.body();
    }
}
