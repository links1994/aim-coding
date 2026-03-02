package com.aim.mall.agent.config;

import com.aim.mall.common.config.BaseSwaggerConfig;
import com.aim.mall.common.domain.SwaggerProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig extends BaseSwaggerConfig {


    @Override
    public SwaggerProperties swaggerProperties() {
        return SwaggerProperties.builder()
                .groupName("mall-agent-service")
                .apiBasePackage(List.of("com.aim.mall.agent"))
                .title("智能员工服务")
                .description("智能员工服务相关接口文档")
                .contactName("aim")
                .version("1.0")
                .enableSecurity(true)
                .build();
    }
}