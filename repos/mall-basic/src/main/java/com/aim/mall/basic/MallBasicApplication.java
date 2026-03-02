package com.aim.mall.basic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * mall-basic 基础服务启动类
 *
 * @author AI Agent
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class MallBasicApplication {

    public static void main(String[] args) {
        SpringApplication.run(MallBasicApplication.class, args);
    }
}
