package org.jiushan.fuzhu.biz.product.router;

import org.jiushan.fuzhu.biz.product.handler.ProductHandler;
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
public class ProductRouter {

    @Bean
    public RouterFunction<ServerResponse> productRouterFunction(ProductHandler productHandler) {
        return RouterFunctions
                .nest(
                        RequestPredicates.path("/api/product"),
                        route(RequestPredicates.POST("/product")
                                        .and(RequestPredicates.accept(MediaType.MULTIPART_FORM_DATA)),
                                productHandler::add)
                                .andRoute(RequestPredicates.DELETE("/product/{id}"), productHandler::remove)
                                .andRoute(RequestPredicates.PUT("/product/{id}"), productHandler::edit)
                                .andRoute(GET("/product/{id}"), productHandler::one)
//                                上下架切换
                                .andRoute(GET("/product/uord/{id}"), productHandler::uord)
                                .andRoute(RequestPredicates.POST("/product/{pageSize}/{pageNow}"), productHandler::page)
                );
    }
}
