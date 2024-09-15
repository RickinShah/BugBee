package com.app.BugBee.repository.custom;

import com.app.BugBee.entity.Reply;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CustomReplyRepository {
    Mono<Reply> saveReply(Reply reply);
    Flux<Reply> findAllByCommentId(long commentId, Pageable pageable);
    Mono<Long> deleteByIdAndUserIdAndCommentId(long replyId, long userId, long commentId);
    Mono<Reply> findByReplyId(long replyId);
}
