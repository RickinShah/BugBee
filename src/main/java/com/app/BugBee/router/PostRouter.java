package com.app.BugBee.router;

import com.app.BugBee.handler.PostHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class PostRouter {
    private final PostHandler handler;

    public PostRouter(PostHandler handler) {
        this.handler = handler;
    }

    @Bean
    public RouterFunction<ServerResponse> postRouting() {
        return RouterFunctions.route()
                .GET("/api/posts", handler::getNextPosts)
                .GET("/api/posts/{postId}", handler::getSinglePost)
                .DELETE("/api/posts/{postId}", handler::deletePost)
                .PATCH("/api/posts/{postId}", handler::updatePost)
                .POST("/api/posts/{postId}", handler::votePost)
                .route(RequestPredicates.POST("/api/auth/posts")
                        .and(RequestPredicates.contentType(MediaType.MULTIPART_FORM_DATA)), handler::insertPost
                )
                .GET("/api/auth/posts/{postId}", handler::decryptAndGetFile)
                .build();
    }
}
