package com.app.BugBee.service;

import com.app.BugBee.dto.BooleanAndMessage;
import com.app.BugBee.dto.UserDto;
import com.app.BugBee.dto.UserRegistrationDto;
import com.app.BugBee.entity.User;
import com.app.BugBee.repository.UserRepository;
import com.app.BugBee.utils.AppUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
public class UserService {
    @Autowired
    private UserRepository repository;

    public Flux<UserDto> getUsers() {
        return repository.findAll().map(AppUtils::UserToDto);
    }

    public Mono<UserDto> getUser(String email) {
        return repository.findByEmail(email)
                .map(AppUtils::UserToDto)
                .defaultIfEmpty(new UserDto(null, null, null));
    }

    public Mono<BooleanAndMessage> saveUser(Mono<UserRegistrationDto> userRegistrationDtoMono) {
        return userRegistrationDtoMono
                .filterWhen(p -> repository.existsByEmail(p.getEmail()).map(e -> !e))
                .map(AppUtils::UserRegistrationToEntity)
                .flatMap(repository::save)
                .map(p -> new BooleanAndMessage(true, "Registered Successfully!"))
                .defaultIfEmpty(new BooleanAndMessage(false, "Email already exists!"));
    }

    public Mono<BooleanAndMessage> updatePassword(Mono<UserRegistrationDto> userRegistrationDtoMono) {
        return userRegistrationDtoMono
                .flatMap(e -> repository.findByEmail(e.getEmail())
                        .doOnNext(obj -> {
                            obj.setPassword(e.getPassword());
                        })
                )
                .doOnNext(e -> System.out.println(e.getId()))
                .flatMap(repository::save)
                .map(p ->new BooleanAndMessage(true, "Password updated!"))
                .defaultIfEmpty(new BooleanAndMessage(false, "Invalid Email!"));
    }

    public Mono<Void> deleteUser(UUID id) {
        return repository.deleteById(id);
    }

    public Flux<UserDto> saveUsers() {
        return Flux.range(1, 50000)
                .map(i -> new User(null, "user" + i, "user" + i, "user" + i, "USER"))
                .flatMap(repository::save).map(AppUtils::UserToDto);
    }

}
