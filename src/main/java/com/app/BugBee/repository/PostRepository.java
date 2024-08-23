package com.app.BugBee.repository;

import com.app.BugBee.entity.Post;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface PostRepository extends R2dbcRepository<Post, Long> {
//    public Flux<Post> findByUserId(UUID uuid);
    Mono<Boolean> deleteByIdAndUserId(long id, long userId);
}
