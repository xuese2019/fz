package org.jiushan.fuzhu.biz.stock.router;

import org.jiushan.fuzhu.biz.stock.handler.StockHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class StockRouter {

    @Bean
    public RouterFunction<ServerResponse> stockRouterFunction(StockHandler stockHandler) {
        return RouterFunctions
                .nest(
                        RequestPredicates.path("/api/stock"),
                        route(RequestPredicates.POST("/stock")
                                        .and(RequestPredicates.accept(MediaType.MULTIPART_FORM_DATA)),
                                stockHandler::add)
                                .andRoute(RequestPredicates.DELETE("/stock/{id}"), stockHandler::remove)
                                .andRoute(RequestPredicates.PUT("/stock/{id}"), stockHandler::edit)
                                .andRoute(GET("/stock/{id}"), stockHandler::one)
                                .andRoute(RequestPredicates.POST("/stock/{pageSize}/{pageNow}"), stockHandler::page)
                );
    }
}
