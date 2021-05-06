package com.github.dingey.gray.web;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(value = "gray.enable")
@ConditionalOnClass(name = "reactor.core.publisher.Mono")
public class GrayWebFilterConfiguration {
    @Bean
    public GrayWebFilter grayWebFilter() {
        return new GrayWebFilter();
    }
}
