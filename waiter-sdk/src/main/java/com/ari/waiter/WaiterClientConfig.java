package com.ari.waiter;

import com.ari.waiter.client.WaiterClient;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Waiter! 开放平台的客户端配置类
 *
 * @author ari24charles
 */
@Configuration
@ConfigurationProperties("waiter.client") // application.yaml 中的元数据配置
@Data
@ComponentScan
public class WaiterClientConfig {

    private String accessKey;

    private String secretKey;

    @Bean
    public WaiterClient waiterClient() {
        return new WaiterClient(accessKey, secretKey);
    }
}