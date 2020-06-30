package org.jiushan.fuzhu.biz.classification.router;

import org.jiushan.fuzhu.biz.classification.handler.ClassificationHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class ClassificationRouter {

    @Bean
    public RouterFunction<ServerResponse> classificationRouterFunction(ClassificationHandler classificationHandler) {
        return RouterFunctions
                .nest(
                        RequestPredicates.path("/api/classification"),
                        route(RequestPredicates.POST("/classification"), classificationHandler::add)
                                .andRoute(RequestPredicates.DELETE("/classification/{id}"), classificationHandler::remove)
                                .andRoute(RequestPredicates.PUT("/classification/{id}"), classificationHandler::edit)
                                .andRoute(GET("/classification/{id}"), classificationHandler::one)
                                .andRoute(RequestPredicates.POST("/classification/{pageSize}/{pageNow}"), classificationHandler::page)
                );
    }
}
