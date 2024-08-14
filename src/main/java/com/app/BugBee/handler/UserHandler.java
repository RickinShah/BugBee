package com.app.BugBee.handler;

import com.app.BugBee.dto.AuthRequest;
import com.app.BugBee.dto.BooleanAndMessage;
import com.app.BugBee.dto.UserDto;
import com.app.BugBee.dto.UserRegistrationDto;
import com.app.BugBee.entity.User;
import com.app.BugBee.enums.ROLES;
import com.app.BugBee.repository.UserRepository;
import com.app.BugBee.security.JwtTokenProvider;
import com.app.BugBee.utils.AppUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
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

    public Mono<ServerResponse> getUsers(ServerRequest request) {
        return ServerResponse.ok().body(BodyInserters.fromPublisher(
                repository.findAll()
                        .map(AppUtils::UserToDto), UserDto.class
        ));
    }

    public Mono<ServerResponse> getUser(ServerRequest request) {
        return request.bodyToMono(String.class)
                .flatMap(repository::findByEmail)
                .map(AppUtils::UserToDto)
                .flatMap(user -> ServerResponse.ok().body(BodyInserters.fromValue(user)));
    }

    public Mono<ServerResponse> saveUser(ServerRequest request) {
        return request.bodyToMono(UserRegistrationDto.class)
                .filterWhen(user -> repository.existsByEmail(user.getEmail()).map(exists -> !exists))
                .map(AppUtils::UserRegistrationToEntity)
                .flatMap(repository::save)
                .map(AppUtils::UserToDto)
                .flatMap(user -> ServerResponse.ok().body(BodyInserters
                                .fromValue(new BooleanAndMessage(true, "Registered Successfully!"))))
                .switchIfEmpty(ServerResponse.badRequest().body(BodyInserters
                        .fromValue(new BooleanAndMessage(false, "Email already exists!"))));
    }

    public Mono<ServerResponse> updatePassword(ServerRequest request) {
        return request.bodyToMono(UserRegistrationDto.class)
                .flatMap(user -> repository.findByEmail(user.getEmail())
                        .doOnNext(e -> e.setPassword(user.getPassword())))
                .flatMap(repository::save)
                .flatMap(e -> ServerResponse.ok()
                        .body(BodyInserters
                                .fromValue(new BooleanAndMessage(true, "Password updated!"))))
                .switchIfEmpty(ServerResponse.badRequest()
                        .body(BodyInserters
                                .fromValue(new BooleanAndMessage(false, "Invalid Email!"))));

    }

    public Mono<ServerResponse> deleteUser(ServerRequest request) {
        String token = request.headers().header(HttpHeaders.AUTHORIZATION).getFirst().substring(7);
        System.out.println(token);
        return repository.deleteByEmail(tokenProvider.getUsername(token))
                .flatMap(e -> ServerResponse.noContent().build());

    }

    public Mono<ServerResponse> saveUsers(ServerRequest request) {
        return ServerResponse.ok().body(Flux.range(1, 100)
                .map(i -> new User(null, "user " + i + "@gmail.com", "user " + i, passwordEncoder.encode("user " + i), ROLES.ROLE_USER.name()))
                .flatMap(repository::save)
                .map(AppUtils::UserToDto), UserDto.class);
    }

    public Mono<ServerResponse> getToken(ServerRequest request) {
        return request.bodyToMono(AuthRequest.class)
                .flatMap(login -> authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(login.getUsername(), login.getPassword())))
                .map(auth -> tokenProvider.createToken(auth))
                .flatMap(jwt -> {
                    Map<String, String> tokenBody = Map.of("id_token", jwt);
                    return ServerResponse.ok().header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt).body(BodyInserters.fromValue(tokenBody));
                });
    }

    public Mono<ServerResponse> validateToken(ServerRequest request) {
        return request.bodyToMono(String.class)
                .map(tokenProvider::validateToken)
                .flatMap(e -> ServerResponse.ok().body(BodyInserters.fromValue(e)));
    }

    public Mono<ServerResponse> addAdmin(ServerRequest request) {
        User user = new User(null, "rickin.shah17403@gmail.com", "Rickin Shah", passwordEncoder.encode("abcd"), ROLES.ROLE_ADMIN.name());
        return repository.save(user)
                .flatMap(e -> ServerResponse.ok().body(BodyInserters.fromValue("Admin added!")));
    }

}
