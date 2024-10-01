package com.app.BugBee.router;

import com.app.BugBee.handler.SearchHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class SearchRouter {
    private final SearchHandler searchHandler;

    public SearchRouter(SearchHandler searchHandler) {
        this.searchHandler = searchHandler;
    }

    @Bean
    public RouterFunction<ServerResponse> searchRouting() {
        return RouterFunctions.route()
                .GET("/api/search", searchHandler::searchUser)
                .build();
    }
}
