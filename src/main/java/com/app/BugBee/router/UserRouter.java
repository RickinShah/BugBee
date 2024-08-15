package com.app.BugBee.router;

import com.app.BugBee.handler.MailHandler;
import com.app.BugBee.handler.UserHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class routerConfig {
    @Autowired
    private UserHandler handler;

    @Autowired
    private MailHandler mailHandler;

    @Bean
    public RouterFunction<ServerResponse> routerFunction() {
        return RouterFunctions.route()
                .POST("/auth/signup", handler::saveUser)
                .POST("/auth/login", handler::getToken)
                .GET("/users/get", handler::getUser)
                .PUT("/users/password/update", handler::updatePassword)
                .DELETE("/users/delete", handler::deleteUser)
                .POST("/admin/save-all", handler::saveUsers)
                .GET("/admin/users", handler::getUsers)
                .GET("/auth/mime-otp", mailHandler::sendOtp)
                .build();
    }
}
