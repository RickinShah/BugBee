package com.app.BugBee.repository;

import com.app.BugBee.entity.ReplyUserVote;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

public interface ReplyVoteRepository extends R2dbcRepository<ReplyUserVote, Long> {
    Mono<Boolean> existsByReplyIdAndUserId(long replyId, long userId);

    Mono<ReplyUserVote> findByReplyIdAndUserId(long replyId, long userId);
}
