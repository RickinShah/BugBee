package com.app.BugBee.handler;

import com.app.BugBee.dto.UserInfoDto;
import com.app.BugBee.mapper.DtoEntityMapper;
import com.app.BugBee.repository.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Service
public class SearchHandler {
    private final UserRepository repository;

    public SearchHandler(UserRepository repository) {
        this.repository = repository;
    }

    public Mono<ServerResponse> searchUser(ServerRequest request) {
        final String keyword = request.queryParam("keyword").orElse("");
        final int offset = Integer.parseInt(request.queryParam("offset").orElse("0"));
        final int size = Integer.parseInt(request.queryParam("size").orElse("0"));

        return ServerResponse.ok().body(BodyInserters.fromPublisher(
                repository.searchUser(keyword, PageRequest.of(offset, size))
                        .map(DtoEntityMapper::userInfoToDto)
                , UserInfoDto.class
        ));
    }
}
