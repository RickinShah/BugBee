package com.app.BugBee.handler;

import com.app.BugBee.dto.BooleanAndMessage;
import com.app.BugBee.dto.PostDto;
import com.app.BugBee.entity.Post;
import com.app.BugBee.enums.TYPE_OF_POST;
import com.app.BugBee.mapper.DtoEntityMapper;
import com.app.BugBee.repository.PostRepository;
import com.app.BugBee.repository.UserRepository;
import com.app.BugBee.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Service
public class PostHandler {
    private final PostRepository repository;

    private final UserRepository userRepository;

    private final JwtTokenProvider tokenProvider;

    public PostHandler(PostRepository repository, UserRepository userRepository, JwtTokenProvider tokenProvider) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.tokenProvider = tokenProvider;
    }

    public Mono<ServerResponse> uploadPost(ServerRequest request) {
        String token = request.headers().header(HttpHeaders.AUTHORIZATION).getFirst().substring(7);
        return request.bodyToMono(Post.class)
                .doOnNext(post -> {
                    post.setUserId(tokenProvider.getUsername(token));
                    post.setNsfw(false);
                    post.setDate(LocalDate.now());
                    post.setTypeOfPost(TYPE_OF_POST.QUESTION.name());
                    post.setUpvote((short) 0);
                    post.setDownvote((short) 0);
                })
                .flatMap(repository::save)
                .flatMap(e -> ServerResponse.ok().body(BodyInserters
                        .fromValue(new BooleanAndMessage(true, "Query inserted successfully!")))
                )
                .switchIfEmpty(ServerResponse.badRequest().body(BodyInserters
                        .fromValue(new BooleanAndMessage(false, "Something went wrong!")))
                );
    }

    public Mono<ServerResponse> getAllPosts(ServerRequest request) {
        return ServerResponse.ok().body(BodyInserters.fromPublisher(
                repository.findAll()
                        .map(DtoEntityMapper::postToDto)
                        .flatMap(post ->
                                userRepository.findById(post.getUserId())
                                .doOnNext(user -> post.setUsername(user.getName()))
                                .map(user -> post)
                        ), PostDto.class
        ));
    }

    public Mono<ServerResponse> deletePost(ServerRequest request) {
        Mono<PostDto> postDtoMono = request.bodyToMono(PostDto.class);
        String token = request.headers().header(HttpHeaders.AUTHORIZATION).getFirst().substring(7);
        long userId = tokenProvider.getUsername(token);
        return postDtoMono
                .flatMap(postDto ->
                        repository.deleteByIdAndUserId(postDto.getId(), userId)
                        .filter(isDeleted -> isDeleted)
                )
                .flatMap(e -> ServerResponse.ok().body(BodyInserters.fromValue(
                        new BooleanAndMessage(true, "Post deleted successfully!")
                )))
                .switchIfEmpty(ServerResponse.badRequest().body(BodyInserters.fromValue(
                        new BooleanAndMessage(false, "Failed to delete the post!")
                )));
    }
}
