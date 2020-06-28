package org.jiushan.fuzhu.routers;

import org.jiushan.fuzhu.biz.user.handler.UserHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

/**
 * 接口api
 */
@Configuration
public class AllRouters {

    @Bean
    public RouterFunction<ServerResponse> initRouterFunction(
            UserHandler userHandler
    ) {
        return RouterFunctions
                .nest(
                        RequestPredicates.path("/user"),
                        RouterFunctions.route(RequestPredicates.POST("/user"), userHandler::add)
                                .andRoute(RequestPredicates.DELETE("/user/{id}"), userHandler::remove)
                                .andRoute(RequestPredicates.PUT("/user/{id}"), userHandler::edit)
                                .andRoute(RequestPredicates.GET("/user/{id}"), userHandler::one)
                                .andRoute(RequestPredicates.POST("/user/{pageSize}/{pageNow}"), userHandler::page)
                )
                .andNest(
                        RequestPredicates.path("/k"),
                        RouterFunctions.route(
                                RequestPredicates.POST("/k")
                                        .and(RequestPredicates.accept(MediaType.TEXT_PLAIN)),
                                userHandler::edit
                        )
                );
    }
}
