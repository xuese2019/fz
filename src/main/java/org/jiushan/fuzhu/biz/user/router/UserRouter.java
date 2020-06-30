package org.jiushan.fuzhu.biz.user.router;

import org.jiushan.fuzhu.biz.user.handler.UserHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
@Configuration
public class UserRouter {

    @Bean
    public RouterFunction<ServerResponse> userRouterFunction(UserHandler userHandler) {
        return RouterFunctions
                .nest(
                        RequestPredicates.path("/api/user"),
                        route(RequestPredicates.POST("/user"), userHandler::add)
                                .andRoute(RequestPredicates.DELETE("/user/{id}"), userHandler::remove)
                                .andRoute(RequestPredicates.PUT("/user/{id}"), userHandler::edit)
                                .andRoute(RequestPredicates.PUT("/user/pwd/{id}"), userHandler::restPwd)
                                .andRoute(GET("/user/{id}"), userHandler::one)
                                .andRoute(RequestPredicates.POST("/user/{pageSize}/{pageNow}"), userHandler::page)
                );
    }
}
