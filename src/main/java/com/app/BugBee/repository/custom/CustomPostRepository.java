package com.app.BugBee.repository.custom;

import com.app.BugBee.entity.Post;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CustomPostRepository {
    Flux<Post> findAll(Pageable pageable);
    Mono<Long> savePost(Post post);
}
