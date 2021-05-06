package com.github.dingey.gray.eureka;

import com.github.dingey.gray.ribbon.ServerFilter;
import com.netflix.niws.loadbalancer.DiscoveryEnabledServer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author d
 */
@Configuration
@ConditionalOnProperty(value = "gray.enable")
@ConditionalOnClass(DiscoveryEnabledServer.class)
public class EurekaConfiguration {
    @Bean
    @ConditionalOnMissingBean
    public ServerFilter serverFilter() {
        return new EurekaServerFilter();
    }
}
