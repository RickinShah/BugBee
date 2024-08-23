package com.app.BugBee.router;

import com.app.BugBee.handler.UserHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class UserRouter {
    private final UserHandler handler;

    public UserRouter(UserHandler handler) {
        this.handler = handler;
    }

    @Bean
    public RouterFunction<ServerResponse> userRouting() {
        return RouterFunctions.route()
                .GET("/users/get", handler::getUser)
                .PUT("/users/password/update", handler::updatePassword)
                .DELETE("/users/delete", handler::deleteUser)
                .build();
    }
}
