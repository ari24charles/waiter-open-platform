package com.ari.waiter.gateway;

import cn.hutool.core.util.StrUtil;
import com.ari.waiter.common.model.entity.InterfaceInfo;
import com.ari.waiter.common.model.entity.User;
import com.ari.waiter.common.service.InnerInterfaceInfoService;
import com.ari.waiter.common.service.InnerUserInterfaceInfoService;
import com.ari.waiter.common.service.InnerUserService;
import com.ari.waiter.utils.SignUtil;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.ORIGINAL_RESPONSE_CONTENT_TYPE_ATTR;

/**
 * 全局过滤
 *
 * @author ari24charles
 */
@Component
@Slf4j
public class CustomGlobalFilter implements GlobalFilter, Ordered {

    @DubboReference
    private InnerUserService innerUserService;

    @DubboReference
    private InnerInterfaceInfoService innerInterfaceInfoService;

    @DubboReference
    private InnerUserInterfaceInfoService innerUserInterfaceInfoService;

    private static final List<String> IP_WHITE_LIST = Arrays.asList("127.0.0.1");

    final long FIVE_MINUTES = 60 * 5L;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 1. 请求日志
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().value();
        String method = request.getMethod().toString();
        log.info("请求唯一标识：" + request.getId());
        log.info("请求路径：" + path);
        log.info("请求方法：" + method);
        String sourceAddress = request.getLocalAddress().getHostString();
        log.info("请求来源地址：" + sourceAddress);
        log.info("请求来源地址：" + request.getRemoteAddress());
        ServerHttpResponse response = exchange.getResponse();
        // 2. 访问控制 - 黑白名单
        if (!IP_WHITE_LIST.contains(sourceAddress)) {
            return handleNoAuth(response);
        }
        // 3. 用户鉴权（判断 ak、sk 是否合法）
        HttpHeaders headers = request.getHeaders();
        String accessKey = headers.getFirst("accessKey");
        String nonce = headers.getFirst("nonce");
        String timestamp = headers.getFirst("timestamp");
        String sign = headers.getFirst("sign");
        String body = headers.getFirst("body");
        /*
        User invokeUser = null;
        try {
            invokeUser = innerUserService.getInvokeUser(accessKey);
        } catch (Exception e) {
            log.error("获取调用用户时发生错误: ", e);
        }
        if (invokeUser == null) {
            return handleNoAuth(response);
        }
         */
        if (nonce == null || Long.parseLong(nonce) >= 100000000L) { // 数据库存储随机数
            return handleNoAuth(response);
        }
        // 时间和当前时间不能超过 5 分钟
        long currentTime = System.currentTimeMillis() / 1000;
        if (timestamp == null || ((currentTime - Long.parseLong(timestamp)) >= FIVE_MINUTES)) { // 防重放
            return handleNoAuth(response);
        }
        // 从数据库中查出 secretKey
        // String secretKey = invokeUser.getSecretKey();
        String secretKey = "ari";
        String serverSign = SignUtil.genSign(body, secretKey);
        if (sign == null || !sign.equals(serverSign)) {
            return handleNoAuth(response); // 签名不一致
        }
        // 4. 请求的模拟接口是否存在，以及请求方法是否匹配
        /*
        InterfaceInfo interfaceInfo = null;
        try {
            interfaceInfo = innerInterfaceInfoService.getInterfaceInfo(path, method);
        } catch (Exception e) {
            log.error("获取接口信息时发生错误", e);
        }
        if (interfaceInfo == null) {
            return handleNoAuth(response);
        }
        // 判断是否还有调用次数
        Long interfaceInfoId = interfaceInfo.getId();
        Long userId = invokeUser.getId();
        if (!innerUserInterfaceInfoService.judgeLeftNum(interfaceInfoId, userId)) {
            return handleNoAuth(response);
        }
         */
        // 5. 请求转发，调用接口 + 响应日志
        // return handleResponse(exchange, chain, interfaceInfoId, userId);
        return handleResponse(exchange, chain, 1, 1);
    }

    @Override
    public int getOrder() {
        return -1;
    }

    public Mono<Void> handleResponse(ServerWebExchange exchange, GatewayFilterChain chain, long interfaceInfoId, long userId) {
        try {
            ServerHttpResponse originalResponse = exchange.getResponse(); // 获得原始响应
            // 缓存数据的工厂，用于创建响应体
            DataBufferFactory bufferFactory = originalResponse.bufferFactory();
            // 拿到响应码
            HttpStatus statusCode = originalResponse.getStatusCode();
            if (statusCode == HttpStatus.OK) {
                ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(originalResponse) {
                    @Override
                    public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                        log.info("body instanceof Flux: {}", (body instanceof Flux));
                        if (body instanceof Flux) {
                            String originalResponseContentType = exchange.getAttribute(ORIGINAL_RESPONSE_CONTENT_TYPE_ATTR);
                            if (StrUtil.isNotBlank(originalResponseContentType) && originalResponseContentType.contains(
                                    "application/json")) { // 处理响应体
                                Flux<? extends DataBuffer> fluxBody = Flux.from(body);
                                // 往返回值里写数据
                                return super.writeWith(
                                        fluxBody.buffer().map(dataBuffers -> { // 分段传输
                                            // 7. 调用成功，接口调用次数 + 1
                                            try {
                                                // innerUserInterfaceInfoService.invokeCount(interfaceInfoId, userId);
                                            } catch (Exception e) {
                                                log.error("接口服务调用统计发生错误", e);
                                            }
                                            DataBufferFactory dataBufferFactory = new DefaultDataBufferFactory();
                                            DataBuffer join = dataBufferFactory.join(dataBuffers);
                                            byte[] content = new byte[join.readableByteCount()];
                                            join.read(content);
                                            DataBufferUtils.release(join);
                                            String responseData = new String(content, StandardCharsets.UTF_8);
                                            byte[] updatedContent = responseData.getBytes();
                                            originalResponse.getHeaders().setContentLength(updatedContent.length);
                                            // 打印日志
                                            log.info("响应结果：" + responseData);
                                            return bufferFactory.wrap(updatedContent);
                                        }));
                            }
                        } else {
                            // 8. 调用失败，返回错误码
                            log.error("<--- {} 响应码异常", getStatusCode());
                        }
                        return super.writeWith(body);
                    }
                };
                // 重建响应
                return chain.filter(exchange.mutate().response(decoratedResponse).build());
            }
            return chain.filter(exchange); // 降级处理返回数据
        } catch (Exception e) {
            log.error("调用接口服务时发生异常" + e);
            return handleInvokeError(exchange.getResponse());
        }
    }

    public Mono<Void> handleNoAuth(ServerHttpResponse response) {
        response.setStatusCode(HttpStatus.FORBIDDEN);
        return response.setComplete();
    }

    public Mono<Void> handleInvokeError(ServerHttpResponse response) {
        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
        return response.setComplete();
    }
}
