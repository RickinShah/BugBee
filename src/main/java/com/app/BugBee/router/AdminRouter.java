package com.app.BugBee.router;

import com.app.BugBee.handler.PostHandler;
import com.app.BugBee.handler.UserHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class AdminRouter {
    private final UserHandler userHandler;

    private final PostHandler postHandler;

    public AdminRouter(UserHandler userHandler, PostHandler postHandler) {
        this.userHandler = userHandler;
        this.postHandler = postHandler;
    }

    @Bean
    public RouterFunction<ServerResponse> adminRouting() {
        return RouterFunctions.route()
                .POST("/admin/save-all", userHandler::saveUsers)
                .GET("/admin/users", userHandler::getUsers)
                .GET("/admin/posts/all", postHandler::getAllPosts)
                .build();
    }
}
