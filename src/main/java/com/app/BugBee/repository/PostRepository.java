package com.app.BugBee.repository;

import com.app.BugBee.entity.Post;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface PostRepository extends R2dbcRepository<Post, Long> {
//    public Flux<Post> findByUserId(UUID uuid);
    Mono<Integer> deleteByIdAndUserId(long id, long userId);
    @Modifying
    @Query("UPDATE bugbee.posts SET upvote = upvote + 1 WHERE id = :id")
    Mono<Boolean> incrementUpvoteById(@Param("id") long id);
    @Modifying
    @Query("UPDATE bugbee.posts SET downvote = downvote + 1 WHERE id = :id")
    Mono<Boolean> incrementDownvoteById(@Param("id") long id);
    @Modifying
    @Query("UPDATE bugbee.posts SET upvote = upvote - 1 WHERE id = :id")
    Mono<Boolean> decrementUpvoteById(@Param("id") long id);
    @Modifying
    @Query("UPDATE bugbee.posts SET downvote = downvote - 1 WHERE id = :id")
    Mono<Boolean> decrementDownvoteById(@Param("id") long id);
}
