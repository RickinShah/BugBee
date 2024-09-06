package com.app.BugBee.repository.custom;

import com.app.BugBee.entity.Comment;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CustomCommentRepository {
    Mono<Comment> saveComment(Comment comment);
    Mono<Long> deleteByIdAndUserIdAndPostId(long commentId, long userId, long postId);
    Flux<Comment> findAllByPostId(long postId, Pageable pageable);
    Mono<Comment> findByCommentId(long commentId);
}
