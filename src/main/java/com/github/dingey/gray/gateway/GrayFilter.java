package com.github.dingey.gray.gateway;

import com.github.dingey.gray.GrayContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author d
 */
public class GrayFilter implements GlobalFilter, Ordered {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String version = exchange.getRequest().getHeaders().getFirst(GrayContext.PREFIX);
        GrayContext.setVersion(version);
        log.debug("gateway网关灰度值：{}",version);
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}