package org.jiushan.fuzhu.sys;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Configuration
@Order(-1)
@Slf4j
public class SysWebFilter implements WebFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange serverWebExchange, WebFilterChain webFilterChain) {
        ServerHttpRequest request = serverWebExchange.getRequest();
        String uri = request.getPath().value();
//        路径判断
        if (uri.contains("/api/") && !uri.contains("/login/")) {
            ServerHttpResponse response = serverWebExchange.getResponse();
            String auth = request.getHeaders().getFirst("auth");
            if (auth == null || auth.isEmpty() || auth.equals("null")) {
                log.info("token过期");
//            未携带token
                response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
                response.getHeaders().setContentType(MediaType.TEXT_HTML);
                return response.writeWith(Mono.just(response.bufferFactory().wrap("logout".getBytes())));
            } else {
//        token验证
            }
        }

        return webFilterChain.filter(serverWebExchange);
    }
}
