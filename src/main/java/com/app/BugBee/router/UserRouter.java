package com.app.BugBee.router;

import com.app.BugBee.handler.UserHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.*;

@Configuration
public class UserRouter {
    private final UserHandler handler;

    public UserRouter(UserHandler handler) {
        this.handler = handler;
    }

    @Bean
    public RouterFunction<ServerResponse> userRouting() {
        return RouterFunctions.route()
                .GET("/users/{username}", handler::userProfile)
                .PATCH("/users/password", handler::updatePassword)
                .DELETE("/users/delete", handler::deleteUser)
                .PATCH("/users/settings", handler::updateProfile)
                .build();
    }
}
