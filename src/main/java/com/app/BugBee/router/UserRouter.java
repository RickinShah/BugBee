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
                .GET("/api/users/{username}", handler::userProfile)
                .PATCH("/api/users/password", handler::updatePassword)
                .DELETE("/api/users/delete", handler::deleteUser)
                .PATCH("/api/users/settings", handler::updateProfile)
                .build();
    }
}
