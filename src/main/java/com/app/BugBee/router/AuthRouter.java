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
public class AuthRouter {

    @Autowired
    private UserHandler userHandler;

    @Autowired
    private MailHandler mailHandler;

    @Bean
    public RouterFunction<ServerResponse> authRouting() {
        return RouterFunctions.route()
                .POST("/auth/signup", userHandler::saveUser)
                .POST("/auth/login", userHandler::getToken)
                .POST("/auth/send-otp", mailHandler::sendOtp)
                .build();
    }
}
