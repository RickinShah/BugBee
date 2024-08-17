package com.app.BugBee.handler;

import com.app.BugBee.dto.UserDto;
import com.app.BugBee.entity.User;
import com.app.BugBee.enums.ROLES;
import com.app.BugBee.mapper.DtoEntityMapper;
import com.app.BugBee.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class AdminHandler {
    @Autowired
    private UserRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Mono<ServerResponse> saveUsers(ServerRequest request) {
        return ServerResponse.ok().body(Flux.range(1, 100)
                .map(i -> new User(null, "user" + i + "@gmail.com", "user " + i, passwordEncoder.encode("user " + i), ROLES.ROLE_USER.name(), false))
                .flatMap(repository::save)
                .map(DtoEntityMapper::userToDto), UserDto.class);
    }

    public Mono<ServerResponse> getUsers(ServerRequest request) {
        return ServerResponse.ok().body(BodyInserters.fromPublisher(
                repository.findAll()
                        .map(DtoEntityMapper::userToDto), UserDto.class
        ));
    }

}
