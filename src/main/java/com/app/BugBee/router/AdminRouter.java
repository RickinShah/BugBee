package com.app.BugBee.router;

//import com.app.BugBee.handler.PostHandler;
import com.app.BugBee.handler.UserHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class AdminRouter {
    private final UserHandler userHandler;

    public AdminRouter(UserHandler userHandler) {
        this.userHandler = userHandler;
    }

    @Bean
    public RouterFunction<ServerResponse> adminRouting() {
        return RouterFunctions.route()
                .GET("/admin/users", userHandler::getUsers)
                .build();
    }
}
