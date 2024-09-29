package com.app.BugBee.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
@Slf4j
public class NsfwHandler {

    private final WebClient webClient;

    public NsfwHandler(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://fastapi").build();
    }

    public Mono<Boolean> checkIfNsfw(String imageUrl, String token) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/posts/check-nsfw")
                        .queryParam("image_url", imageUrl)
                        .build()
                )
                .header("Cookie", "token=Bearer " + token)
                .retrieve()
                .bodyToMono(Map.class)
                .map(map -> (Integer) map.get("nsfw") == 1);
    }
}
