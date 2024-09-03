package com.app.BugBee.handler;

import com.app.BugBee.dto.AuthRequest;
import com.app.BugBee.dto.BooleanAndMessage;
import com.app.BugBee.dto.UserDto;
import com.app.BugBee.entity.User;
import com.app.BugBee.enums.PROFILES;
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

    public Mono<ServerResponse> userProfile(ServerRequest request) {
//        log.info("Called!");
        String username = request.pathVariable("username");
        return repository.findByUsername(username)
                .map(DtoEntityMapper::userToDto)
                .flatMap(userDto -> ServerResponse.ok().body(BodyInserters.fromValue(
                        userDto
                )));
    }

    public Mono<ServerResponse> signUp(ServerRequest request) {
        Mono<User> userMono = request.bodyToMono(User.class)
                .doOnNext(user -> {
                    user.setProfile(PROFILES.P1.name());
                    user.setRoles(ROLES.ROLE_USER.name());
                    user.setPassword(passwordEncoder.encode(user.getPassword()));
                });
        return userMono
                .doOnNext(user -> log.info(user.toString()))
                .flatMap(user -> checkIfUsernameOrEmailAlreadyExists(user)
//                                .doOnNext(booleanAndMessage -> log.info(booleanAndMessage.toString()))
                                .flatMap(booleanAndMessage ->
                                        booleanAndMessage.isSuccess() ?
                                                ServerResponse.badRequest().body(
                                                        BodyInserters.fromValue(booleanAndMessage)) :
                                                repository.saveUser(user)
                                                        .flatMap(userNew -> ServerResponse.ok()
                                                                .body(BodyInserters.fromValue(
                                                                        new BooleanAndMessage(true, "Sign up successful")
                                                                )))
                                )
                );
    }

    public Mono<ServerResponse> updatePassword(ServerRequest request) {
        String username = request.pathVariable("username");
        Mono<User> userMono = request.bodyToMono(User.class).doOnNext(user -> user.setUsername(username));
        return userMono
                .flatMap(user -> repository.findByUsername(user.getUsername())
                        .doOnNext(e -> e.setPassword(passwordEncoder.encode(user.getPassword()))))
                .doOnNext(e->log.info(e.toString()))
                .flatMap(repository::saveUser)
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

    public Mono<ServerResponse> loginAndGetToken(ServerRequest request) {
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

    public Mono<ServerResponse> getUsers(ServerRequest request) {
        return ServerResponse.ok().body(BodyInserters.fromPublisher(
                repository.findAll()
                        .map(DtoEntityMapper::userToDto), UserDto.class)
        );
    }

    public Mono<ServerResponse> updateProfile(ServerRequest request) {
        final long userId = tokenProvider.getUsername(request.headers().header(HttpHeaders.AUTHORIZATION).getFirst().substring(7));
        final Mono<UserDto> userDtoMono = request.bodyToMono(UserDto.class)
                .doOnNext(userDto -> userDto.setUserId(userId));
        return userDtoMono
                .map(DtoEntityMapper::dtoToUser)
                .flatMap(user -> repository.findByUserId(user.getUserId())
                        .doOnNext(userNew -> {
                            userNew.setProfile(user.getProfile());
                            userNew.setEmail(user.getEmail());
                            userNew.setPassword(passwordEncoder.encode(user.getPassword()));
                            userNew.setUsername(user.getUsername());
                            userNew.setName(user.getName());
                            userNew.setShowNsfw(user.isShowNsfw());
                            userNew.setBio(user.getBio());
                        })
                )
                .flatMap(repository::saveUser)
                .map(DtoEntityMapper::userToDto)
                .flatMap(userDto -> ServerResponse.ok().body(BodyInserters.fromValue(
                        userDto
                )));
    }


    // Internal Methods below

    private Mono<BooleanAndMessage> checkIfUsernameOrEmailAlreadyExists(User user) {
        return repository.existsByEmail(user.getEmail())
                .flatMap(exists -> exists ?
                        Mono.just(new BooleanAndMessage(true, "Email already exists!")) :
                        repository.existsByUsername(user.getUsername())
                                .map(usernameExists -> usernameExists ?
                                        new BooleanAndMessage(true, "Username already exists") :
                                        new BooleanAndMessage(false, "No Duplicates!"))
                );
    }

}
