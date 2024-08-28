//package com.app.BugBee.router;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.reactive.function.server.RouterFunction;
//import org.springframework.web.reactive.function.server.RouterFunctions;
//import org.springframework.web.reactive.function.server.ServerResponse;
//
//@Configuration
//public class ReplyRouter {
//    private final ReplyHandler handler;
//
//    public ReplyRouter(ReplyHandler replyHandler) {
//        this.handler = replyHandler;
//    }
//
//    @Bean
//    public RouterFunction<ServerResponse> replyRouting() {
//        return RouterFunctions.route()
//                .GET("/comments/{commentId}/replies", handler::getRepliesByCommentId)
//                .POST("/comments/{commentId}/replies", handler::insertReplyByCommentId)
//                .PATCH("/comments/{commentId}/replies/{replyId}", handler::editReply)
//                .DELETE("/comments/{commentId}/replies/{replyId}", handler::deleteReply)
//                .build();
//    }
//}
