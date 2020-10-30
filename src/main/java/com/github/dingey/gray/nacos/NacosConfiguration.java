package com.github.dingey.gray.nacos;

import com.alibaba.cloud.nacos.ribbon.NacosServer;
import com.github.dingey.gray.ribbon.ServerFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author d
 */
@Configuration
@ConditionalOnProperty(value = "gray.enable")
@ConditionalOnClass(NacosServer.class)
public class NacosConfiguration {
    @Bean
    public ServerFilter serverFilter() {
        return new NacosServerFilter();
    }
}
