package com.github.dingey.gray.web;

import com.github.dingey.gray.GrayContext;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * @author d
 */
public class GrayWebFilter implements WebFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange serverWebExchange, WebFilterChain webFilterChain) {

        ServerHttpRequest request = serverWebExchange.getRequest();

        String version = request.getHeaders().getFirst(GrayContext.PREFIX);
        GrayContext.setVersion(version);

        return webFilterChain.filter(serverWebExchange);
    }
}
