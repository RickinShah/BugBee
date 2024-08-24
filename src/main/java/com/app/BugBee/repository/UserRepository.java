package com.app.BugBee.repository;

import com.app.BugBee.entity.User;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface UserRepository extends R2dbcRepository<User, Long> {
    Mono<Boolean> existsByEmail(String email);
    Mono<Boolean> existsByUsername(String username);
    Mono<User> findByEmail(String email);
    Mono<User> findByUsernameOrEmail(String username, String email);
    Mono<User> findByUsername(String username);
}
