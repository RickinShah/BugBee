package com.app.BugBee.handler;

import com.app.BugBee.dto.AuthRequest;
import com.app.BugBee.dto.BooleanAndMessage;
import com.app.BugBee.dto.UserDto;
import com.app.BugBee.entity.User;
import com.app.BugBee.enums.ROLES;
import com.app.BugBee.mapper.DtoEntityMapper;
import com.app.BugBee.repository.UserRepository;
import com.app.BugBee.security.JwtTokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
@Slf4j
public class UserHandler {
    private final UserRepository repository;

    private final ReactiveAuthenticationManager authenticationManager;

    private final JwtTokenProvider tokenProvider;

    private final PasswordEncoder passwordEncoder;

    public UserHandler(UserRepository repository, ReactiveAuthenticationManager authenticationManager, JwtTokenProvider tokenProvider, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.passwordEncoder = passwordEncoder;
    }

    public Mono<ServerResponse> getUser(ServerRequest request) {
        return request.bodyToMono(String.class)
                .flatMap(repository::findByEmail)
                .map(DtoEntityMapper::userToDto)
                .flatMap(user -> ServerResponse.ok().body(BodyInserters.fromValue(user)));
    }

    public Mono<ServerResponse> signUp(ServerRequest request) {
        Mono<User> userMono = request.bodyToMono(User.class);
        return userMono
                .doOnNext(user -> log.info(user.toString()))
                .flatMap(user -> checkIfUsernameOrEmailALreadyExists(user)
                                .doOnNext(booleanAndMessage -> log.info(booleanAndMessage.toString()))
                                .flatMap(booleanAndMessage -> booleanAndMessage.isSuccess() ?
                                        ServerResponse.badRequest().body(BodyInserters.fromValue(booleanAndMessage)) :
                                        saveUser(user)
                                                .flatMap(boolAndMessage ->
                                                        ServerResponse.ok()
                                                                .body(BodyInserters.fromValue(boolAndMessage)))
                                )
                );
    }

    public Mono<ServerResponse> updatePassword(ServerRequest request) {
        return request.bodyToMono(User.class)
                .flatMap(user -> repository.findByEmail(user.getEmail())
                        .doOnNext(e -> e.setPassword(passwordEncoder.encode(user.getPassword()))))
                .flatMap(repository::save)
                .flatMap(e -> ServerResponse.ok()
                        .body(BodyInserters
                                .fromValue(new BooleanAndMessage(true, "Password updated!")))
                )
                .switchIfEmpty(ServerResponse.badRequest()
                        .body(BodyInserters
                                .fromValue(new BooleanAndMessage(false, "Invalid Email!")))
                );

    }

    public Mono<ServerResponse> deleteUser(ServerRequest request) {
        String token = request.headers().header(HttpHeaders.AUTHORIZATION).getFirst().substring(7);
        return repository.deleteById(tokenProvider.getUsername(token))
                .then(ServerResponse.ok().build());
    }

    public Mono<ServerResponse> getToken(ServerRequest request) {
        return request.bodyToMono(AuthRequest.class)
                .flatMap(login -> authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(login.getUsername(), login.getPassword())
                ))
                .filter(Authentication::isAuthenticated)
                .switchIfEmpty(Mono.error(new RuntimeException("Authentication failed")))
                .map(tokenProvider::createToken)
                .flatMap(jwt -> {
                    Map<String, String> tokenBody = Map.of("id_token", jwt);
                    return ServerResponse.ok()
//                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt)
                            .body(BodyInserters.fromValue(tokenBody)
                            );
                })
                .onErrorResume(RuntimeException.class, e -> ServerResponse.badRequest().body(BodyInserters.fromValue(
                        new BooleanAndMessage(false, e.getMessage())
                )));
    }

    public Mono<ServerResponse> saveUsers(ServerRequest request) {
        return ServerResponse.ok().body(Flux.range(1, 100)
                .map(i -> new User(0, "user" + i, "user" + i + "@gmail.com", "user " + i, passwordEncoder.encode("user " + i), ROLES.ROLE_USER.name(), false))
                .flatMap(repository::save)
                .map(DtoEntityMapper::userToDto), UserDto.class);
    }

    public Mono<ServerResponse> getUsers(ServerRequest request) {
        return ServerResponse.ok().body(BodyInserters.fromPublisher(
                repository.findAll()
                        .map(DtoEntityMapper::userToDto), UserDto.class)
        );
    }

    public Mono<BooleanAndMessage> checkIfUsernameOrEmailALreadyExists(User user) {
        return repository.existsByEmail(user.getEmail())
                .flatMap(exists -> exists ?
                        Mono.just(new BooleanAndMessage(true, "Email already exists!")) :
                        repository.existsByUsername(user.getUsername())
                                .map(usernameExists -> usernameExists ?
                                        new BooleanAndMessage(true, "Username already exists") :
                                        new BooleanAndMessage(false, "No Duplicates!"))
                );
    }

    public Mono<BooleanAndMessage> saveUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return repository.save(user)
                .thenReturn(new BooleanAndMessage(true, "Registered Successfully!"));
    }
}
