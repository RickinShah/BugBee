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
                .GET("/posts", handler::getNextPosts)
                .GET("/posts/{postId}", handler::getSinglePost)
                .DELETE("/posts/{postId}", handler::deletePost)
                .PATCH("/posts/{postId}", handler::updatePost)
                .POST("/posts/{postId}", handler::votePost)
                .route(RequestPredicates.POST("/auth/posts")
                        .and(RequestPredicates.contentType(MediaType.MULTIPART_FORM_DATA)), handler::insertPost
                )
                .build();
    }
}
