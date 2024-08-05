package com.app.BugBee.handler;

import com.app.BugBee.dto.BooleanAndMessage;
import com.app.BugBee.dto.UserDto;
import com.app.BugBee.dto.UserRegistrationDto;
import com.app.BugBee.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.rsocket.RSocketProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
public class UserHandler {
    @Autowired
    private UserService service;

    public Mono<ServerResponse> getUsers(ServerRequest request) {
        return ServerResponse.ok().body(service.getUsers(), UserDto.class);
    }

    public Mono<ServerResponse> getUser(ServerRequest request) {
        Mono<String> email = request.bodyToMono(String.class);
        return email.flatMap(e -> ServerResponse.ok().body(service.getUser(e), UserDto.class)).log();
    }

    public Mono<ServerResponse> saveUser(ServerRequest request) {
        Mono<UserRegistrationDto> userRegistrationDtoMono = request.bodyToMono(UserRegistrationDto.class);
        return service.saveUser(userRegistrationDtoMono)
                .flatMap(p -> p.isBool()?
                        ServerResponse.ok().body(Mono.just(p), BooleanAndMessage.class) :
                        ServerResponse.badRequest().body(Mono.just(p), BooleanAndMessage.class));
    }

    public Mono<ServerResponse> updatePassword(ServerRequest request) {
        Mono<UserRegistrationDto> userRegistrationDtoMono = request.bodyToMono(UserRegistrationDto.class);
        return service.updatePassword(userRegistrationDtoMono)
                .flatMap(p-> p.isBool()?
                        ServerResponse.ok().body(Mono.just(p), BooleanAndMessage.class) :
                        ServerResponse.badRequest().body(Mono.just(p), BooleanAndMessage.class));
    }

    public Mono<ServerResponse> deleteUser(ServerRequest request) {
        UUID uuid = UUID.fromString(request.headers().header("token").getFirst());
        return service.deleteUser(uuid).flatMap(e -> ServerResponse.noContent().build());
    }

    public Mono<ServerResponse> saveUsers(ServerRequest request) {
        return ServerResponse.ok().body(service.saveUsers(), UserDto.class);
    }

}
