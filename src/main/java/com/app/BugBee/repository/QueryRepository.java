package com.app.BugBee.repository;

import com.app.BugBee.entity.Question;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Repository
public interface QueryRepository extends R2dbcRepository<Question, UUID> {
    public Flux<Question> findByUserId(UUID uuid);
}
