package com.app.BugBee.router;

import com.app.BugBee.handler.OtpHandler;
import com.app.BugBee.handler.UserHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class AuthRouter {

    private final UserHandler userHandler;

    private final OtpHandler otpHandler;

    public AuthRouter(UserHandler userHandler, OtpHandler otpHandler) {
        this.userHandler = userHandler;
        this.otpHandler = otpHandler;
    }

    @Bean
    public RouterFunction<ServerResponse> authRouting() {
        return RouterFunctions.route()
                .POST("/api/auth/signup", userHandler::signUp)
                .POST("/api/auth/login", userHandler::loginAndGetToken)
                .POST("/api/auth/otp", otpHandler::sendOtp)
                .POST("/api/auth/otp/{username}", otpHandler::validateOtp)
                .PATCH("/api/auth/otp/{username}", userHandler::updatePassword)
                .build();
    }
}
