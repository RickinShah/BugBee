package com.app.BugBee.repository.custom;

import com.app.BugBee.entity.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CustomUserRepository {
    Mono<User> findByUsername(String username);

    Mono<User> findByUsernameOrEmail(String username, String email);

    Mono<User> findByUserId(long userId);

    Mono<User> saveUser(User user);

    Flux<User> findAll();
}
