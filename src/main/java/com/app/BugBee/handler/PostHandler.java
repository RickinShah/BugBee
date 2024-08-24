package com.app.BugBee.handler;

import com.app.BugBee.dto.BooleanAndMessage;
import com.app.BugBee.dto.PostDto;
import com.app.BugBee.entity.PostVote;
import com.app.BugBee.enums.TYPE_OF_POST;
import com.app.BugBee.mapper.DtoEntityMapper;
import com.app.BugBee.repository.PostRepository;
import com.app.BugBee.repository.PostVoteRepository;
import com.app.BugBee.repository.UserRepository;
import com.app.BugBee.security.JwtTokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@Service
@Slf4j
public class PostHandler {
    private final PostRepository repository;

    private final UserRepository userRepository;

    private final JwtTokenProvider tokenProvider;

    private final PostVoteRepository postVoteRepository;

    public PostHandler(PostRepository repository, UserRepository userRepository, JwtTokenProvider tokenProvider, PostVoteRepository postVoteRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.tokenProvider = tokenProvider;
        this.postVoteRepository = postVoteRepository;
    }

    public Mono<ServerResponse> uploadPost(ServerRequest request) {
        String token = request.headers().header(HttpHeaders.AUTHORIZATION).getFirst().substring(7);
        Mono<PostDto> postDtoMono = request.bodyToMono(PostDto.class)
                .map(postDto -> PostDto.builder()
                        .userId(tokenProvider.getUsername(token))
                        .title(postDto.getTitle())
                        .post(postDto.getPost())
                        .isNsfw(false)
                        .date(LocalDate.now())
                        .typeOfPost(TYPE_OF_POST.QUESTION.name())
                        .upvote((short) 0)
                        .downvote((short) 0)
                        .build()
                );
        return postDtoMono
                .map(DtoEntityMapper::dtoToPost)
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

//    public Mono<ServerResponse> deletePost(ServerRequest request) {
//        Mono<PostDto> postDtoMono = request.bodyToMono(PostDto.class);
//        String token = request.headers().header(HttpHeaders.AUTHORIZATION).getFirst().substring(7);
//        long userId = tokenProvider.getUsername(token);
//        return postDtoMono
//                .flatMap(postDto ->
//                        repository.deleteByIdAndUserId(postDto.getId(), userId)
//                        .filter(isDeleted -> isDeleted)
//                )
//                .flatMap(e -> ServerResponse.ok().body(BodyInserters.fromValue(
//                        new BooleanAndMessage(true, "Post deleted successfully!")
//                )))
//                .switchIfEmpty(ServerResponse.badRequest().body(BodyInserters.fromValue(
//                        new BooleanAndMessage(false, "Failed to delete the post!")
//                )));
//    }

    public Mono<ServerResponse> votePost(ServerRequest request) {
        String token = request.headers().header(HttpHeaders.AUTHORIZATION).getFirst().substring(7);
        Mono<PostVote> postVoteMono = request.bodyToMono(PostVote.class);

        return postVoteMono
                .doOnNext(postVote -> postVote.setUserId(tokenProvider.getUsername(token)))
                .flatMap(postVote -> postVoteRepository.findByUserIdAndPostId(
                                postVote.getUserId(),
                                postVote.getPostId())
                        .next()
                        .defaultIfEmpty(new PostVote())
//                        .doOnNext(vote -> log.info(vote.toString()))
                        .filter(vote -> vote.getId() != 0)
//                        .doOnNext(vote -> log.info(vote.toString()))
                        .flatMap(vote -> upvoteOrDownvoteIfAlreadyExists(vote, postVote))
                        .switchIfEmpty(upvoteOrDownvoteIfNotExists(postVote))
                )
                .flatMap(booleanAndMessage -> ServerResponse.ok().body(BodyInserters.fromValue(booleanAndMessage)));

    }

    public Mono<BooleanAndMessage> upvoteOrDownvoteIfNotExists(PostVote postVote) {
        log.info(postVote.toString());
        return postVoteRepository.save(postVote)
                .flatMap(postVote1 -> postVote1.isTypeOfVote() ?
                        repository.incrementUpvoteById(postVote1.getPostId()) :
                        repository.incrementDownvoteById(postVote1.getPostId())
                )
//                .doOnNext(e -> log.info(e.toString()))
                .map(e -> new BooleanAndMessage(true, "Upvoted/Downvoted successfully!"))
                .switchIfEmpty(Mono.just(new BooleanAndMessage(false, "Failed!")));
    }

    public Mono<BooleanAndMessage> upvoteOrDownvoteIfAlreadyExists(PostVote dbPostVote, PostVote postVote) {
        return dbPostVote.isTypeOfVote() == postVote.isTypeOfVote() ?
                deleteVote(dbPostVote) :
                toggleVote(dbPostVote);
    }

    public Mono<BooleanAndMessage> deleteVote(PostVote postVote) {
        return postVoteRepository.delete(postVote)
                .then(postVote.isTypeOfVote() ? repository.decrementUpvoteById(postVote.getPostId()) :
                        repository.decrementDownvoteById(postVote.getPostId()))
                .thenReturn(new BooleanAndMessage(true, "Deleted Vote!"));
    }

    public Mono<BooleanAndMessage> toggleVote(PostVote postVote) {
        postVote.setTypeOfVote(!postVote.isTypeOfVote());
        return postVote.isTypeOfVote() ?
                repository.decrementDownvoteById(postVote.getPostId())
                        .flatMap(e -> postVoteRepository.save(postVote))
                        .flatMap(e -> repository.incrementUpvoteById(postVote.getPostId()))
                        .thenReturn(new BooleanAndMessage(true, "Upvoted!")) :
                repository.decrementUpvoteById(postVote.getPostId())
                        .flatMap(e -> postVoteRepository.save(postVote))
                        .flatMap(e -> repository.incrementDownvoteById(postVote.getPostId()))
                        .thenReturn(new BooleanAndMessage(true, "Downvoted!"));


    }

}
