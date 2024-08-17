package com.app.BugBee.handler;

import com.app.BugBee.dto.BooleanAndMessage;
import com.app.BugBee.dto.QueryDto;
import com.app.BugBee.dto.QueryUserDto;
import com.app.BugBee.entity.Question;
import com.app.BugBee.mapper.DtoEntityMapper;
import com.app.BugBee.repository.QueryRepository;
import com.app.BugBee.repository.UserRepository;
import com.app.BugBee.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Service
public class QueryHandler {
    @Autowired
    private QueryRepository repository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenProvider tokenProvider;

    public Mono<ServerResponse> insertQuery(ServerRequest request) {
        String token = request.headers().header(HttpHeaders.AUTHORIZATION).getFirst().substring(7);
        return request.bodyToMono(Question.class)
                .doOnNext(question -> question.setUserId(tokenProvider.getUsername(token)))
                .flatMap(repository::save)
                .flatMap(e -> ServerResponse.ok().body(BodyInserters
                        .fromValue(new BooleanAndMessage(true, "Query inserted successfully!")))
                )
                .switchIfEmpty(ServerResponse.badRequest().body(BodyInserters
                        .fromValue(new BooleanAndMessage(false, "Something went wrong!")))
                );
    }

    public Mono<ServerResponse> getQueryWithUser(ServerRequest request) {
        return request.bodyToMono(Question.class)
                .flatMap(question -> repository.findById(question.getId()))
                .flatMap(question -> userRepository.findById(question.getUserId())
                        .map(user -> new QueryUserDto(
                                DtoEntityMapper.queryToDto(question),
                                DtoEntityMapper.userToDto(user))
                        )
                )
                .flatMap(queryUser -> ServerResponse.ok().body(BodyInserters.fromValue(queryUser)));
    }

    public Mono<ServerResponse> getQueriesByUserId(ServerRequest request) {
        return ServerResponse.ok().body(BodyInserters.fromPublisher(
                request.bodyToMono(Question.class)
                        .flatMapMany(question -> repository.findByUserId(question.getUserId()))
                        .map(DtoEntityMapper::queryToDto), QueryDto.class
        ));
    }
}
