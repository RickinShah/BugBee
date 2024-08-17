package com.app.BugBee.handler;

import com.app.BugBee.dto.AuthRequest;
import com.app.BugBee.dto.BooleanAndMessage;
import com.app.BugBee.entity.User;
import com.app.BugBee.mapper.DtoEntityMapper;
import com.app.BugBee.repository.UserRepository;
import com.app.BugBee.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
public class UserHandler {
    @Autowired
    private UserRepository repository;

    @Autowired
    private ReactiveAuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Mono<ServerResponse> getUser(ServerRequest request) {
        return request.bodyToMono(String.class)
                .flatMap(repository::findByEmail)
                .map(DtoEntityMapper::userToDto)
                .flatMap(user -> ServerResponse.ok().body(BodyInserters.fromValue(user)));
    }

    public Mono<ServerResponse> saveUser(ServerRequest request) {
        return request.bodyToMono(User.class)
                .filterWhen(user -> repository.existsByEmail(user.getEmail()).map(exists -> !exists))
                .flatMap(repository::save)
                .flatMap(user -> ServerResponse.ok().body(BodyInserters
                        .fromValue(new BooleanAndMessage(true, "Registered Successfully!")))
                )
                .switchIfEmpty(ServerResponse.badRequest().body(BodyInserters
                        .fromValue(new BooleanAndMessage(false, "Email already exists!")))
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
                .flatMap(e -> ServerResponse.noContent().build());

    }

    public Mono<ServerResponse> getToken(ServerRequest request) {
        return request.bodyToMono(AuthRequest.class)
                .flatMap(login -> authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(login.getUsername(), login.getPassword())))
                .map(auth -> tokenProvider.createToken(auth))
                .flatMap(jwt -> {
                    Map<String, String> tokenBody = Map.of("id_token", jwt);
                    return ServerResponse.ok().header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt)
                            .body(BodyInserters.fromValue(tokenBody));
                });
    }

}
