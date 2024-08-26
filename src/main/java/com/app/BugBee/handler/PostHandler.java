package com.app.BugBee.handler;

import com.app.BugBee.dto.BooleanAndMessage;
import com.app.BugBee.dto.PostDto;
import com.app.BugBee.dto.UserDto;
import com.app.BugBee.entity.Post;
import com.app.BugBee.entity.PostVote;
import com.app.BugBee.entity.User;
import com.app.BugBee.enums.TYPE_OF_POST;
import com.app.BugBee.mapper.DtoEntityMapper;
import com.app.BugBee.repository.PostRepository;
import com.app.BugBee.repository.PostVoteRepository;
import com.app.BugBee.repository.UserRepository;
import com.app.BugBee.security.JwtTokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
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
        Mono<PostDto> postDtoMono = request.bodyToMono(PostDto.class).doOnNext(post -> {
            post.setTypeOfPost(TYPE_OF_POST.QUESTION.name());
            post.setDate(LocalDate.now());
            post.setUser(new UserDto(tokenProvider.getUsername(token), null, null, null, false));
            log.info(post.getUser().toString());
        });
        return postDtoMono
                .map(DtoEntityMapper::dtoToPost)
                .doOnNext(e -> log.info(e.getUser().toString()))
                .flatMap(repository::savePost)
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
                                userRepository.findById(post.getUser().getUserId())
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
        Mono<PostVote> postVoteMono = request.bodyToMono(PostVote.class)
                .doOnNext(postVote -> postVote.setUserId(tokenProvider.getUsername(token)));

        return postVoteMono
                .flatMap(postVote -> postVoteRepository.findByUserIdAndPostId(
                                postVote.getUserId(),
                                postVote.getPostId())
                        .next()
                        .defaultIfEmpty(new PostVote())
//                        .doOnNext(vote -> log.info(vote.toString()))
                        .filter(vote -> vote.getPostVoteId() != 0)
//                        .doOnNext(vote -> log.info(vote.toString()))
                        .flatMap(vote -> upvoteOrDownvoteIfAlreadyExists(vote, postVote))
                        .switchIfEmpty(upvoteOrDownvoteIfNotExists(postVote))
                )
                .flatMap(booleanAndMessage -> ServerResponse.ok().body(BodyInserters.fromValue(booleanAndMessage)));

    }

    public Mono<ServerResponse> getLatestPosts(ServerRequest request) {
        Mono<Integer> lastIdMono = request.bodyToMono(Integer.class).defaultIfEmpty(0);
//        log.info("getLatestPost called!");
        return ServerResponse.ok().body(BodyInserters.fromPublisher(
            lastIdMono
                    .flatMapMany(lastId -> repository.findAll(PageRequest.of(lastId, 5)))
                    .map(DtoEntityMapper::postToDto)
                , PostDto.class
        ));
    }

    public Mono<BooleanAndMessage> upvoteOrDownvoteIfNotExists(PostVote postVote) {
        log.info(postVote.toString());
        return postVoteRepository.save(postVote)
                .flatMap(postVote1 -> postVote1.isUpvote() ?
                        repository.incrementUpvoteByPostId(postVote1.getPostId()) :
                        repository.incrementDownvoteByPostId(postVote1.getPostId())
                )
//                .doOnNext(e -> log.info(e.toString()))
                .map(e -> new BooleanAndMessage(true, "Upvoted/Downvoted successfully!"))
                .switchIfEmpty(Mono.just(new BooleanAndMessage(false, "Failed!")));
    }

    public Mono<BooleanAndMessage> upvoteOrDownvoteIfAlreadyExists(PostVote dbPostVote, PostVote postVote) {
        return dbPostVote.isUpvote() == postVote.isUpvote() ?
                deleteVote(dbPostVote) :
                toggleVote(dbPostVote);
    }

    public Mono<BooleanAndMessage> deleteVote(PostVote postVote) {
        return postVoteRepository.delete(postVote)
                .then(postVote.isUpvote() ? repository.decrementUpvoteByPostId(postVote.getPostId()) :
                        repository.decrementDownvoteByPostId(postVote.getPostId()))
                .thenReturn(new BooleanAndMessage(true, "Deleted Vote!"));
    }

    public Mono<BooleanAndMessage> toggleVote(PostVote postVote) {
        postVote.setUpvote(!postVote.isUpvote());
        return postVote.isUpvote() ?
                repository.decrementDownvoteByPostId(postVote.getPostId())
                        .flatMap(e -> postVoteRepository.save(postVote))
                        .flatMap(e -> repository.incrementUpvoteByPostId(postVote.getPostId()))
                        .thenReturn(new BooleanAndMessage(true, "Upvoted!")) :
                repository.decrementUpvoteByPostId(postVote.getPostId())
                        .flatMap(e -> postVoteRepository.save(postVote))
                        .flatMap(e -> repository.incrementDownvoteByPostId(postVote.getPostId()))
                        .thenReturn(new BooleanAndMessage(true, "Downvoted!"));


    }

}
