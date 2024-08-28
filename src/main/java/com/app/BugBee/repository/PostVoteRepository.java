package com.app.BugBee.repository;

import com.app.BugBee.entity.PostUserVote;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface PostVoteRepository extends R2dbcRepository<PostUserVote, Long> {
    Mono<PostUserVote> findByUserIdAndPostId(long userId, long postId);
}
