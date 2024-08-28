package com.app.BugBee.repository;

import com.app.BugBee.entity.User;
import com.app.BugBee.repository.custom.CustomUserRepository;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface UserRepository extends R2dbcRepository<User, Long>, CustomUserRepository {
    Mono<Boolean> existsByEmail(String email);

    Mono<Boolean> existsByUsername(String username);
}
