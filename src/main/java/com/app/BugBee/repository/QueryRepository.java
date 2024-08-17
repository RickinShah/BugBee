package com.app.BugBee.repository;

import com.app.BugBee.entity.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Repository
public interface QueryRepository extends R2dbcRepository<Query, UUID> {
    public Flux<Query> findByUserId(UUID uuid);
}
