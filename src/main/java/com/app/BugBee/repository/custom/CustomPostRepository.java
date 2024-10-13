package com.app.BugBee.repository.custom;

import com.app.BugBee.entity.Post;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CustomPostRepository {
    Flux<Post> findAll(int size, long lastId);

    Mono<Post> savePost(Post post);

    Mono<Post> findByPostId(Long postId);

    Mono<Long> deleteByPostIdAndUserId(Long postId, Long userId);
}
