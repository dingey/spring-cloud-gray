package com.github.dingey.gray.feign;

import com.github.dingey.gray.GrayContext;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author d
 */
@Configuration
@ConditionalOnProperty(value = "gray.enable")
@ConditionalOnClass(name = "feign.RequestInterceptor")
public class GrayFeignConfiguration {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Bean
    public GrayFeignRequestInterceptor grayFeignRequestInterceptor() {
        if (log.isDebugEnabled()) {
            log.debug("Initializing Gray Feign client support");
        }
        return new GrayFeignRequestInterceptor();
    }

    class GrayFeignRequestInterceptor implements RequestInterceptor {
        protected transient Log logger = LogFactory.getLog(this.getClass());

        @Override
        public void apply(RequestTemplate requestTemplate) {
            logger.debug("判断是否传递灰度上下文变量");
            if (GrayContext.isGray()) {
                logger.debug("传递灰度版本" + GrayContext.getVersion());
                requestTemplate.header(GrayContext.PREFIX, GrayContext.getVersion());
            }
        }
    }
}
