package org.jiushan.fuzhu.routers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

/**
 * 接口api
 */
@Configuration
public class PageRouters {

    /**
     * 页面跳转
     *
     * @param indexHtml
     * @return
     */
    @Bean
    public RouterFunction<ServerResponse> htmlRouter(
            @Value("classpath:/templates/index.html") final Resource indexHtml,
            @Value("classpath:/templates/home/home.html") final Resource homeHtml,
            @Value("classpath:/templates/home/index.html") final Resource homeIndexHtml,
            @Value("classpath:/templates/user/page.html") final Resource userPageHtml,
            @Value("classpath:/templates/classification/page.html") final Resource classificationPageHtml
    ) {
        return RouterFunctions.route(RequestPredicates.GET("/"),
                r -> ServerResponse.ok().contentType(MediaType.TEXT_HTML).bodyValue(indexHtml))
                .andRoute(RequestPredicates.GET("/home"),
                        r -> ServerResponse.ok().contentType(MediaType.TEXT_HTML).bodyValue(homeHtml))
                .andRoute(RequestPredicates.GET("/home/index"),
                        r -> ServerResponse.ok().contentType(MediaType.TEXT_HTML).bodyValue(homeIndexHtml))
                .andRoute(RequestPredicates.GET("/user/page"),
                        r -> ServerResponse.ok().contentType(MediaType.TEXT_HTML).bodyValue(userPageHtml))
                .andRoute(RequestPredicates.GET("/classification/page"),
                        r -> ServerResponse.ok().contentType(MediaType.TEXT_HTML).bodyValue(classificationPageHtml))
                ;
    }
}
