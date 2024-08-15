package com.app.BugBee.router;

import com.app.BugBee.handler.AdminHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class AdminRouter {
    @Autowired
    private AdminHandler handler;

    @Bean
    public RouterFunction<ServerResponse> adminRouting() {
        return RouterFunctions.route()
                .POST("/admin/save-all", handler::saveUsers)
                .GET("/admin/users", handler::getUsers)
                .build();
    }
}
