package com.ari.waiter;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Waiter! 核心业务启动类
 *
 * @author ari24charles
 */
@SpringBootApplication
@MapperScan({"com.ari.waiter.mapper"})
@EnableDubbo
public class WaiterApplication {

    public static void main(String[] args) {
        SpringApplication.run(WaiterApplication.class, args);
    }
}