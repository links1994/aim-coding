package com.aim.mall;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableFeignClients(basePackages = "com.aim.mall.*.api")
@EnableScheduling
@SpringBootApplication(scanBasePackages = {
        "com.aim.mall"})
@MapperScan({
        "com.aim.mall.agent.mapper"
})
public class MallAgentEmployeeApplication {
    public static void main(String[] args) {
        SpringApplication.run(MallAgentEmployeeApplication.class, args);
    }
}
