package com.app.BugBee.repository;

import com.app.BugBee.entity.PostVote;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface PostVoteRepository extends R2dbcRepository<PostVote, Long> {
    Flux<PostVote> findByUserIdAndPostId(long userId, long postId);
}
