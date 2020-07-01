package org.jiushan.fuzhu.biz.file.router;

import org.jiushan.fuzhu.biz.file.handler.FileHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class FileRouter {

    @Bean
    public RouterFunction<ServerResponse> fileRouterFunction(FileHandler fileHandler) {
        return RouterFunctions
                .nest(
                        RequestPredicates.path("/api/file"),
                        route(RequestPredicates.POST("/upload"), fileHandler::upload)
                                .andRoute(GET("/dow/{id}"), fileHandler::dow)
                );
    }
}
