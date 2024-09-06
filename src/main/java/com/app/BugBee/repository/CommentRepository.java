package com.app.BugBee.repository;

import com.app.BugBee.entity.Comment;
import com.app.BugBee.repository.custom.CustomCommentRepository;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface CommentRepository extends R2dbcRepository<Comment, Long>, CustomCommentRepository {

    @Modifying
    @Query("UPDATE bugbee.comments SET upvote_count = upvote_count + 1 WHERE comment_pid = :commentId")
    Mono<Boolean> incrementUpvoteByCommentId(@Param("commentId") long commentId);

    @Modifying
    @Query("UPDATE bugbee.comments SET upvote_count = upvote_count - 1 WHERE comment_pid = :commentId")
    Mono<Boolean> decrementUpvoteByCommentId(@Param("commentId") long commentId);

    @Modifying
    @Query("UPDATE bugbee.comments SET downvote_count = downvote_count + 1 WHERE comment_pid = :commentId")
    Mono<Boolean> incrementDownvoteByCommentId(@Param("commentId") long commentId);

    @Modifying
    @Query("UPDATE bugbee.comments SET downvote_count = downvote_count - 1 WHERE comment_pid = :commentId")
    Mono<Boolean> decrementDownvoteByCommentId(@Param("commentId") long commentId);

    @Modifying
    @Query("UPDATE bugbee.comments SET upvote_count = upvote_count + 1, downvote_count = downvote_count - 1 WHERE comment_pid = :commentId")
    Mono<Boolean> incrementUpvoteAndDecrementDownvote(@Param("commentId") long commentId);

    @Modifying
    @Query("UPDATE bugbee.comments SET upvote_count = upvote_count - 1, downvote_count = downvote_count + 1 WHERE comment_pid = :commentId")
    Mono<Boolean> decrementUpvoteAndIncrementDownvote(@Param("commentId") long commentId);
}
