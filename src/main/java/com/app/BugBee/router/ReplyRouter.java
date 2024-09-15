package com.app.BugBee.router;

import com.app.BugBee.handler.ReplyHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class ReplyRouter {
    private final ReplyHandler handler;

    public ReplyRouter(ReplyHandler replyHandler) {
        this.handler = replyHandler;
    }

    @Bean
    public RouterFunction<ServerResponse> replyRouting() {
        return RouterFunctions.route()
                .GET("/api/comments/{commentId}/replies", handler::getRepliesByCommentId)
                .POST("/api/comments/{commentId}/replies", handler::insertReplyByCommentId)
                .PATCH("/api/comments/{commentId}/replies/{replyId}", handler::editReply)
                .DELETE("/api/comments/{commentId}/replies/{replyId}", handler::deleteReply)
                .PUT("/api/comments/{commentId}/replies/{replyId}", handler::voteReply)
                .build();
    }
}
