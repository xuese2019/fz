package org.jiushan.fuzhu.biz.login.router;

import org.jiushan.fuzhu.biz.login.handler.LoginHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class LoginRouter {

    @Bean
    public RouterFunction<ServerResponse> loginRouterFunction(LoginHandler loginHandler) {
        return RouterFunctions
                .nest(
                        RequestPredicates.path("/api/login"),
                        route(RequestPredicates.POST("/login").and(accept(MediaType.APPLICATION_FORM_URLENCODED)),
                                loginHandler::login)
                );
    }
}
