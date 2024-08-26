package com.app.BugBee.repository;

import com.app.BugBee.entity.Post;
import com.app.BugBee.repository.custom.CustomPostRepository;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface PostRepository extends R2dbcRepository<Post, Long>, CustomPostRepository {
//    public Flux<Post> findByUserId(UUID uuid);
//    Mono<Integer> deleteByPostIdAndUserId(long postId, long userId);
    @Modifying
    @Query("UPDATE bugbee.posts SET upvote = upvote + 1 WHERE post_id = :post_id")
    Mono<Boolean> incrementUpvoteByPostId(@Param("post_id") long postId);
    @Modifying
    @Query("UPDATE bugbee.posts SET downvote = downvote + 1 WHERE post_id = :post_id")
    Mono<Boolean> incrementDownvoteByPostId(@Param("post_id") long postId);
    @Modifying
    @Query("UPDATE bugbee.posts SET upvote = upvote - 1 WHERE post_id = :post_id")
    Mono<Boolean> decrementUpvoteByPostId(@Param("post_id") long postId);
    @Modifying
    @Query("UPDATE bugbee.posts SET downvote = downvote - 1 WHERE post_id = :post_id")
    Mono<Boolean> decrementDownvoteByPostId(@Param("post_id") long postId);
//    Flux<Post> findAll(Pageable pageable);
}
