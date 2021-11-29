package com.github.dingey.gray;

import com.github.dingey.gray.ribbon.GrayRandomRule;
import com.netflix.loadbalancer.IRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * @author d
 */
@EnableConfigurationProperties(GrayProperties.class)
@ConditionalOnProperty(value = "gray.enable")
@ConditionalOnClass(name = "com.netflix.client.IClientConfigAware")
@Configuration
public class GrayConfiguration {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Bean
    @Scope(value = "prototype")
    public IRule grayRoundRobinRule() {
        if (log.isDebugEnabled()) {
            log.debug("Initializing Spring cloud Gray support");
        }
        return new GrayRandomRule();
    }
}
