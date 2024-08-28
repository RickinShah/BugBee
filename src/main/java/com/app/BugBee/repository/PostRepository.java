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
    Mono<Integer> deleteByPostId(long postId);

    @Modifying
    @Query("UPDATE bugbee.posts SET upvote_count = upvote_count + 1 WHERE post_pid = :postId")
    Mono<Boolean> incrementUpvoteByPostId(@Param("postId") long postId);

    @Modifying
    @Query("UPDATE bugbee.posts SET downvote_count = downvote_count + 1 WHERE post_pid = :postId")
    Mono<Boolean> incrementDownvoteByPostId(@Param("postId") long postId);

    @Modifying
    @Query("UPDATE bugbee.posts SET upvote_count = upvote_count - 1 WHERE post_pid = :postId")
    Mono<Boolean> decrementUpvoteByPostId(@Param("postId") long postId);

    @Modifying
    @Query("UPDATE bugbee.posts SET downvote_count = downvote_count - 1 WHERE post_pid = :postId")
    Mono<Boolean> decrementDownvoteByPostId(@Param("postId") long postId);
}
