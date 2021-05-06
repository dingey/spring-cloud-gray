package com.github.dingey.gray.webmvc;

import com.github.dingey.gray.GrayContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author d
 */
@Configuration
@ConditionalOnProperty(value = "gray.enable")
@ConditionalOnClass(name = "org.springframework.web.servlet.config.annotation.WebMvcConfigurer")
public class GrayWebMvcConfiguration implements WebMvcConfigurer {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        if (log.isInfoEnabled()) {
            log.info("Initializing Gray spring webmvc support");
        }
        registry.addInterceptor(new GrayInterceptor());
    }

    class GrayInterceptor implements HandlerInterceptor {
        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
            String version = request.getHeader(GrayContext.PREFIX);
            if (StringUtils.hasText(version)) {
                log.debug("灰度传递版本version:{}", version);
                GrayContext.setVersion(version);
            }
            return true;
        }

        @Override
        public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
            if (StringUtils.hasText(request.getHeader(GrayContext.PREFIX))) {
                log.debug("灰度清除版本version:" + request.getHeader(GrayContext.PREFIX));
                GrayContext.removeVersion();
            }
        }
    }
}
