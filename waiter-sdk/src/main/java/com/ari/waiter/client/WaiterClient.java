package com.ari.waiter.client;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.ari.waiter.utils.SignUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.AllArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * 调用 Waiter! 开放平台所提供接口的客户端
 *
 * @author ari24charles
 */
@AllArgsConstructor
public class WaiterClient {

    private static final String GATEWAY_ADDR = "http://localhost:8280"; // 网关地址

    private final String accessKey; // 密钥

    private final String secretKey; // 签名密钥

    /**
     * 生成请求头参数
     *
     * @param body 序列化后的请求体对象
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
     * @param uri        接口 URI /api/user/get?name=ari&age=22
     * @return 响应体对象
     */
    public Object get(String uri) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.serializeNulls(); // 不自动过滤 null
        Gson gson = gsonBuilder.create();
        HttpResponse response = HttpRequest.get(GATEWAY_ADDR + uri)
                .addHeaders(getHeader(uri)) //
                .execute();
        return gson.fromJson(response.body(), Object.class);
    }

    /**
     * 发送 POST 请求到接口
     *
     * @param uri           接口 URI /api/user/get
     * @param requestParams 请求体对象
     * @return 响应体对象
     */
    public Object post(String uri, Object requestParams) {
        String json;
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.serializeNulls(); // 不自动过滤 null
        Gson gson = gsonBuilder.create();
        if (requestParams == null) {
            json = "";
        } else {
            json = gson.toJson(requestParams);
        }
        HttpResponse response = HttpRequest.post(GATEWAY_ADDR + uri)
                .addHeaders(getHeader(json))
                .body(json)
                .execute();
        return gson.fromJson(response.body(), Object.class);
    }
}
