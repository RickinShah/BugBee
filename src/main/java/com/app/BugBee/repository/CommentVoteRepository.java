package com.app.BugBee.repository;

import com.app.BugBee.entity.Comment;
import com.app.BugBee.entity.CommentUserVote;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface CommentVoteRepository extends R2dbcRepository<CommentUserVote, Long> {
    Mono<Boolean> existsByCommentIdAndUserId(Long commentId, Long userId);
    Mono<CommentUserVote> findByCommentId(long commentId);
}
