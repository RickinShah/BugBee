package com.app.BugBee.repository;

import com.app.BugBee.entity.Reply;
import com.app.BugBee.repository.custom.CustomReplyRepository;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface ReplyRepository extends R2dbcRepository<Reply, Long>, CustomReplyRepository {
    @Modifying
    @Query("UPDATE bugbee.replies SET upvote_count = upvote_count + 1 WHERE reply_pid = :replyId")
    Mono<Boolean> incrementUpvoteByReplyId(@Param("replyId") long replyId);

    @Modifying
    @Query("UPDATE bugbee.replies SET upvote_count = upvote_count - 1 WHERE reply_pid = :replyId")
    Mono<Boolean> decrementUpvoteByReplyId(@Param("replyId") long replyId);

    @Modifying
    @Query("UPDATE bugbee.replies SET downvote_count = downvote_count + 1 WHERE reply_pid = :replyId")
    Mono<Boolean> incrementDownvoteByReplyId(@Param("replyId") long replyId);

    @Modifying
    @Query("UPDATE bugbee.replies SET downvote_count = downvote_count - 1 WHERE reply_pid = :replyId")
    Mono<Boolean> decrementDownvoteByReplyId(@Param("replyId") long replyId);

    @Modifying
    @Query("UPDATE bugbee.replies SET upvote_count = upvote_count + 1, downvote_count = downvote_count - 1 WHERE reply_pid = :replyId")
    Mono<Boolean> incrementUpvoteAndDecrementDownvote(@Param("replyId") long replyId);

    @Modifying
    @Query("UPDATE bugbee.replies SET upvote_count = upvote_count - 1, downvote_count = downvote_count + 1 WHERE reply_pid = :replyId")
    Mono<Boolean> decrementUpvoteAndIncrementDownvote(@Param("replyId") long replyId);
}
