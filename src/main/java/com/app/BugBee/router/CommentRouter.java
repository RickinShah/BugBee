package com.app.BugBee.router;

import com.app.BugBee.handler.CommentHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class CommentRouter {
    private final CommentHandler handler;

    public CommentRouter(CommentHandler handler) {
        this.handler = handler;
    }

    @Bean
    public RouterFunction<ServerResponse> commentRouting() {
        return RouterFunctions.route()
                .GET("/api/posts/{postId}/comments", handler::getCommentsByPostId)
                .POST("/api/posts/{postId}/comments", handler::insertCommentByPostId)
                .PATCH("/api/posts/{postId}/comments/{commentId}", handler::editComment)
                .DELETE("/api/posts/{postId}/comments/{commentId}", handler::deleteComment)
                .PUT("/api/posts/{postId}/comments/{commentId}", handler::voteComment)
                .build();
    }
}
