package com.github.dingey.gray.gateway;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(value = "gray.enable")
@ConditionalOnClass(name = "org.springframework.cloud.gateway.filter.GlobalFilter")
public class GrayFilterConfiguration {
    @Bean
    public GrayFilter grayFilter() {
        return new GrayFilter();
    }
}
