package com.app.BugBee.handler;

import com.app.BugBee.dto.BooleanAndMessage;
import com.app.BugBee.dto.PostDto;
import com.app.BugBee.dto.UserInfoDto;
import com.app.BugBee.entity.PostUserVote;
import com.app.BugBee.enums.POST_TYPE;
import com.app.BugBee.mapper.DtoEntityMapper;
import com.app.BugBee.repository.PostRepository;
import com.app.BugBee.repository.PostVoteRepository;
import com.app.BugBee.security.JwtTokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@Service
@Slf4j
public class PostHandler {
    private final PostRepository repository;

    private final JwtTokenProvider tokenProvider;

    private final PostVoteRepository postVoteRepository;

    public PostHandler(PostRepository repository, JwtTokenProvider tokenProvider, PostVoteRepository postVoteRepository) {
        this.repository = repository;
        this.tokenProvider = tokenProvider;
        this.postVoteRepository = postVoteRepository;
    }

    public Mono<ServerResponse> insertPost(ServerRequest request) {
        final long userId = tokenProvider.getUsername(request.headers().header(HttpHeaders.AUTHORIZATION).getFirst().substring(7));
        final Mono<PostDto> postDtoMono = request.bodyToMono(PostDto.class).doOnNext(post -> {
            post.setPostType(POST_TYPE.QUESTION.name());
            post.setUpdatedAt(LocalDate.now());
            post.setUser(UserInfoDto.builder().userId(userId).build());
//            log.info(post.getUser().toString());
        });
        return postDtoMono
                .map(DtoEntityMapper::dtoToPost)
//                .doOnNext(e -> log.info(e.getUser().toString()))
                .flatMap(repository::savePost)
                .flatMap(e -> ServerResponse.ok().body(BodyInserters
                        .fromValue(new BooleanAndMessage(true, "Query inserted successfully!")))
                )
                .switchIfEmpty(ServerResponse.badRequest().body(BodyInserters
                        .fromValue(new BooleanAndMessage(false, "Something went wrong!")))
                );
    }

    public Mono<ServerResponse> updatePost(ServerRequest request) {
        final long postId = Long.parseLong(request.pathVariable("postId"));
        Mono<PostDto> postDtoMono = request.bodyToMono(PostDto.class)
                .doOnNext(postDto -> {
                    postDto.setPostId(postId);
                    postDto.setUpdatedAt(LocalDate.now());
                });
        return postDtoMono
                .map(DtoEntityMapper::dtoToPost)
                .flatMap(repository::savePost)
                .flatMap(e -> e == 0 ?
                        ServerResponse.ok().body(BodyInserters.fromValue(
                                new BooleanAndMessage(false, "Nothing to delete!")
                        )) :
                        ServerResponse.ok().body(BodyInserters.fromValue(
                                new BooleanAndMessage(true, "Post updated successfully!")
                        )));
    }

    public Mono<ServerResponse> deletePost(ServerRequest request) {
        final long userId = tokenProvider.getUsername(request.headers().header(HttpHeaders.AUTHORIZATION).getFirst().substring(7));
        final long postId = Long.parseLong(request.pathVariable("postId"));

        return repository.deleteByPostIdAndUserId(postId, userId)
                .then(ServerResponse.noContent().build());
    }

    public Mono<ServerResponse> votePost(ServerRequest request) {
        final long userId = tokenProvider.getUsername(request.headers().header(HttpHeaders.AUTHORIZATION).getFirst().substring(7));
        final long postId = Long.parseLong(request.pathVariable("postId"));
        final Mono<PostUserVote> postUserVoteMono = request.bodyToMono(PostUserVote.class)
                .doOnNext(postUserVote -> {
                    postUserVote.setPostId(postId);
                    postUserVote.setUserId(userId);
                });
//                .doOnNext(postUserVote -> log.info("{}",postUserVote.isVoteStatus()));

        return postUserVoteMono
                .flatMap(postUserVote -> postVoteRepository.findByUserIdAndPostId(
                        postUserVote.getUserId(), postUserVote.getPostId()
                                )
                        .defaultIfEmpty(new PostUserVote())
//                        .doOnNext(vote -> log.info(vote.toString()))
                        .filter(vote -> vote.getPostId() != 0)
//                        .doOnNext(vote -> log.info(vote.toString()))
                        .flatMap(vote -> upvoteOrDownvoteIfAlreadyExists(vote, postUserVote))
                        .switchIfEmpty(upvoteOrDownvoteIfNotExists(postUserVote))
                )
                .flatMap(booleanAndMessage -> booleanAndMessage.isSuccess() ?
                        ServerResponse.ok().build() :
                        ServerResponse.badRequest().body(BodyInserters.fromValue(booleanAndMessage))
                );

    }

    public Mono<ServerResponse> getNextPosts(ServerRequest request) {
        final long userId = tokenProvider.getUsername(request.headers().header(HttpHeaders.AUTHORIZATION).getFirst().substring(7));
        MultiValueMap<String, String> offsetAndSize = request.queryParams();
        return ServerResponse.ok().body(BodyInserters.fromPublisher(
                repository.findAll(PageRequest.of(
                                Integer.parseInt(offsetAndSize.getFirst("offset")),
                                Integer.parseInt(offsetAndSize.getFirst("size"))))
                        .map(DtoEntityMapper::postToDto)
                        .flatMap(postDto ->
                                postVoteRepository.findByUserIdAndPostId(
                                                userId, postDto.getPostId()
                                        )
                                        .doOnNext(postUserVote -> {
                                            postDto.setVoteStatus(postUserVote.isVoteStatus());
                                            postDto.setVotedFlag(true);
                                        })
                                        .map(postUserVote -> postDto)
                                        .switchIfEmpty(Mono.just(postDto))
                        )
                , PostDto.class
        ));
    }

    public Mono<ServerResponse> getSinglePost(ServerRequest request) {
        final long userId = tokenProvider.getUsername(request.headers().header(HttpHeaders.AUTHORIZATION).getFirst().substring(7));
        final long postId = Long.parseLong(request.pathVariable("postId"));

        return repository.findByPostId(postId)
                .map(DtoEntityMapper::postToDto)
                .flatMap(postDto ->
                        postVoteRepository.findByUserIdAndPostId(
                                        userId, postDto.getPostId()
                                )
                                .doOnNext(postUserVote -> {
                                    postDto.setVoteStatus(postUserVote.isVoteStatus());
                                    postDto.setVotedFlag(true);
                                })
                                .map(postUserVote -> postDto)
                                .flatMap(postDto1 -> ServerResponse.ok().body(BodyInserters.fromValue(postDto1)))
                                .switchIfEmpty(ServerResponse.ok().body(BodyInserters.fromValue(postDto)))
                );
    }


    // Internal Methods Below

    private Mono<BooleanAndMessage> upvoteOrDownvoteIfNotExists(PostUserVote postUserVote) {
//        log.info(postVote.toString());
        return postVoteRepository.save(postUserVote)
                .flatMap(postUserVote1 -> postUserVote1.isVoteStatus() ?
                        repository.incrementUpvoteByPostId(postUserVote1.getPostId()) :
                        repository.incrementDownvoteByPostId(postUserVote1.getPostId())
                )
//                .doOnNext(e -> log.info(e.toString()))
                .map(e -> new BooleanAndMessage(true, "Upvoted/Downvoted successfully!"))
                .switchIfEmpty(Mono.just(new BooleanAndMessage(false, "Failed!")));
    }

    private Mono<BooleanAndMessage> upvoteOrDownvoteIfAlreadyExists(PostUserVote dbPostUserVote, PostUserVote postUserVote) {
        return dbPostUserVote.isVoteStatus() == postUserVote.isVoteStatus() ?
                deleteVote(dbPostUserVote) :
                toggleVote(dbPostUserVote);
    }

    private Mono<BooleanAndMessage> deleteVote(PostUserVote postUserVote) {
        return postVoteRepository.delete(postUserVote)
                .then(postUserVote.isVoteStatus() ? repository.decrementUpvoteByPostId(postUserVote.getPostId()) :
                        repository.decrementDownvoteByPostId(postUserVote.getPostId()))
                .thenReturn(new BooleanAndMessage(true, "Deleted Vote!"));
    }

    private Mono<BooleanAndMessage> toggleVote(PostUserVote postUserVote) {
        postUserVote.setVoteStatus(!postUserVote.isVoteStatus());
        return postUserVote.isVoteStatus() ?
                repository.decrementDownvoteByPostId(postUserVote.getPostId())
                        .flatMap(e -> postVoteRepository.save(postUserVote))
                        .flatMap(e -> repository.incrementUpvoteByPostId(postUserVote.getPostId()))
                        .thenReturn(new BooleanAndMessage(true, "Upvoted!")) :
                repository.decrementUpvoteByPostId(postUserVote.getPostId())
                        .flatMap(e -> postVoteRepository.save(postUserVote))
                        .flatMap(e -> repository.incrementDownvoteByPostId(postUserVote.getPostId()))
                        .thenReturn(new BooleanAndMessage(true, "Downvoted!"));


    }

}
