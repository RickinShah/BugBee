package com.app.BugBee.router;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class CommentRouter {
//    private final CommentHandler handler;
//
//    public CommentRouter(CommentHandler commentHandler) {
//        this.handler = commentHandler;
//    }
//
//    @Bean
//    public RouterFunction<ServerResponse> commentRouting() {
//        return RouterFunctions.route()
//                .GET("/posts/{postId}/comments", handler::getCommentsByPostId)
//                .POST("/posts/{postId}/comments", handler::insertCommentByPostId)
//                .PATCH("/posts/{postId}/comments/{commentId}", handler::editComment)
//                .DELETE("/posts/{psotId}/comments/{commentId}")
//                .build();
//
//    }
}
