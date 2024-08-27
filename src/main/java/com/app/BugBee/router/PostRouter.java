package com.app.BugBee.router;

import com.app.BugBee.handler.PostHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
                .POST("/posts/upload", handler::uploadPost)
                .POST("/posts/upvote/{postId}", handler::votePost)
                .PUT("/posts/update/{postId}", handler::updatePost)
                .GET("/posts/next", handler::getLatestPosts)
                .DELETE("/posts/delete/{postId}", handler::deletePost)
                .build();
    }
}
