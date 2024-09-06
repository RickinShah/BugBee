package com.app.BugBee.handler;

import com.app.BugBee.dto.*;
import com.app.BugBee.entity.CommentUserVote;
import com.app.BugBee.mapper.DtoEntityMapper;
import com.app.BugBee.repository.CommentRepository;
import com.app.BugBee.repository.CommentVoteRepository;
import com.app.BugBee.security.JwtTokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.Map;

@Service
@Slf4j
public class CommentHandler {
    private final CommentRepository repository;
    private final JwtTokenProvider tokenProvider;
    private final CommentVoteRepository commentVoteRepository;

    public CommentHandler(CommentRepository repository, JwtTokenProvider tokenProvider, CommentVoteRepository commentVoteRepository) {
        this.repository = repository;
        this.tokenProvider = tokenProvider;
        this.commentVoteRepository = commentVoteRepository;
    }

    public Mono<ServerResponse> getCommentsByPostId(ServerRequest request) {
        final long postId = Long.parseLong(request.pathVariable("postId"));
        final MultiValueMap<String, String> offsetAndSize = request.queryParams();

        return ServerResponse.ok().body(BodyInserters.fromPublisher(
                repository
                        .findAllByPostId(postId, PageRequest.of(
                                Integer.parseInt(offsetAndSize.getFirst("offset")),
                                Integer.parseInt(offsetAndSize.getFirst("size"))
                        ))
                        .map(DtoEntityMapper::commentToDto), CommentDto.class
        ))
                .onErrorResume(Exception.class, e -> {
                    log.info(e.getMessage());
                    return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                            BodyInserters.fromValue(
                                    Map.of("error", "An unexpected error occurred. Please try again later!"))
                    );
                });

    }

    public Mono<ServerResponse> voteComment(ServerRequest request) {
        final long userId = tokenProvider.getUsername(request.headers().header(HttpHeaders.AUTHORIZATION).getFirst().substring(7));
        final long commentId = Long.parseLong(request.pathVariable("commentId"));
        final Mono<CommentUserVote> commentUserVoteMono = request.bodyToMono(CommentUserVote.class)
                .doOnNext(commentUserVote -> {
                    commentUserVote.setCommentId(commentId);
                    commentUserVote.setUserId(userId);
                });

        return commentUserVoteMono
                .flatMap(commentUserVote ->
                        commentVoteRepository.existsByCommentIdAndUserId(
                                commentUserVote.getCommentId(),
                                commentUserVote.getUserId()
                        )
                        .flatMap(exists -> exists ?
                                upvoteOrDowvoteIfAlreadyExists(commentUserVote) :
                                upvoteOrDownvoteIfNotExists(commentUserVote)
                        )
                )
                .flatMap(e -> ServerResponse.ok().body(BodyInserters.fromValue(
                        new BooleanAndMessage(true, "Upvoted")
                )))
                .onErrorResume(Exception.class, e -> {
                    log.info(e.getMessage());
                    return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                            BodyInserters.fromValue(
                                    Map.of("error", "An unexpected error occurred. Please try again later!"))
                    );
                });
    }

    public Mono<ServerResponse> insertCommentByPostId(ServerRequest request) {
        final long userId = tokenProvider.getUsername(request.headers().header(HttpHeaders.AUTHORIZATION).getFirst().substring(7));
        final long postId = Long.parseLong(request.pathVariable("postId"));
        final Mono<CommentDto> commentDtoMono = request.bodyToMono(CommentDto.class)
                .doOnNext(commentDto -> log.info("{}", commentDto))
                .doOnNext(commentDto -> {
                    commentDto.setPost(PostDto.builder().postId(postId).build());
                    commentDto.setUser(UserInfoDto.builder().userId(userId).build());
                    commentDto.setUpdatedAt(LocalDate.now());
                    commentDto.setUpdateFlag(false);
                });

        return commentDtoMono
                .doOnNext(commentDto -> log.info("{}", commentDto))
                .map(DtoEntityMapper::dtoToComment)
                .doOnNext(commentDto -> log.info("{}", commentDto))
                .flatMap(repository::saveComment)
                .flatMap(e -> ServerResponse.ok().body(BodyInserters.fromValue(
                        new BooleanAndMessage(true, "Comment Added!")
                )))
                .switchIfEmpty(ServerResponse.badRequest().body(BodyInserters.fromValue(
                        new BooleanAndMessage(false, "Comment Not Added!")
                )))
                .onErrorResume(Exception.class, e -> {
                    log.info(e.getMessage());
                    return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                            BodyInserters.fromValue(
                                    Map.of("error", "An unexpected error occurred. Please try again later!"))
                    );
                });

    }

    public Mono<ServerResponse> editComment(ServerRequest request) {
        final long userId = tokenProvider.getUsername(request.headers().header(HttpHeaders.AUTHORIZATION).getFirst().substring(7));
        final long postId = Long.parseLong(request.pathVariable("postId"));
        final long commentId = Long.parseLong(request.pathVariable("commentId"));
        final Mono<CommentDto> commentDtoMono = request.bodyToMono(CommentDto.class)
                .doOnNext(commentDto -> {
                    commentDto.setPost(PostDto.builder().postId(postId).build());
                    commentDto.setUser(UserInfoDto.builder().userId(userId).build());
                    commentDto.setCommentId(commentId);
                    commentDto.setUpdatedAt(LocalDate.now());
                });

        return commentDtoMono
                .map(DtoEntityMapper::dtoToComment)
                .flatMap(repository::saveComment)
                .flatMap(comment -> ServerResponse.ok().body(BodyInserters.fromValue(
                        new BooleanAndMessage(true, "Comment Updated!")
                )))
                .switchIfEmpty(ServerResponse.notFound().build())
                .onErrorResume(Exception.class, e -> {
                    log.info(e.getMessage());
                    return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                            BodyInserters.fromValue(
                                    Map.of("error", "An unexpected error occurred. Please try again later!"))
                    );
                });

    }

    public Mono<ServerResponse> deleteComment(ServerRequest request) {
        final long userId = tokenProvider.getUsername(request.headers().header(HttpHeaders.AUTHORIZATION).getFirst().substring(7));
        final long postId = Long.parseLong(request.pathVariable("postId"));
        final long commentId = Long.parseLong(request.pathVariable("commentId"));

        return repository.deleteByIdAndUserIdAndPostId(commentId, userId, postId)
                .flatMap(deleted -> deleted == 0 ?
                        ServerResponse.notFound().build() :
                        ServerResponse.ok().build()
                )
                .onErrorResume(Exception.class, e -> {
                    log.info(e.getMessage());
                    return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                            BodyInserters.fromValue(
                                    Map.of("error", "An unexpected error occurred. Please try again later!"))
                    );
                });
    }

    // Internal Methods Below

    private Mono<BooleanAndMessage> upvoteOrDownvoteIfNotExists(CommentUserVote commentUserVote) {
        return commentVoteRepository.save(commentUserVote)
                .flatMap(commentUserVote1 -> commentUserVote1.isVoteStatus() ?
                        repository.incrementUpvoteByCommentId(commentUserVote1.getCommentId()) :
                        repository.incrementDownvoteByCommentId(commentUserVote1.getCommentId())
                )
                .map(voted -> new BooleanAndMessage(voted, "Upvoted/Downvoted"))
                .switchIfEmpty(Mono.error(new RuntimeException("Couldn't Upvote/Downvote")));
    }

    private Mono<BooleanAndMessage> upvoteOrDowvoteIfAlreadyExists(CommentUserVote commentUserVote) {
        return Mono.fromCallable(() -> commentUserVote)
                .flatMap(commentUserVote1 -> commentVoteRepository.findByCommentId(commentUserVote.getCommentId())
                        .doOnNext(commentUserVote2 -> commentUserVote1.setCommentId(commentUserVote2.getCommentId()))
                        .flatMap(commentUserVote2 -> commentUserVote1.isVoteStatus() == commentUserVote2.isVoteStatus() ?
                                deleteVote(commentUserVote1) :
                                toggleVote(commentUserVote1)
                        )
                )
                .map(booleanAndMessage -> booleanAndMessage);
    }

    private Mono<BooleanAndMessage> deleteVote(CommentUserVote commentUserVote) {
        return commentVoteRepository.delete(commentUserVote)
                .then(commentUserVote.isVoteStatus() ?
                        repository.decrementUpvoteByCommentId(commentUserVote.getCommentId()) :
                        repository.decrementDownvoteByCommentId(commentUserVote.getCommentId())
                )
                .map(unvoted -> new BooleanAndMessage(unvoted, "Removed Vote"))
                .switchIfEmpty(Mono.error(new RuntimeException("Couldn't Remove Vote")));
    }

    private Mono<BooleanAndMessage> toggleVote(CommentUserVote commentUserVote) {
        return commentVoteRepository.save(commentUserVote)
                .flatMap(commentUserVote1 -> commentUserVote1.isVoteStatus() ?
                        repository.incrementUpvoteAndDecrementDownvote(commentUserVote1.getCommentId()) :
                        repository.decrementUpvoteAndIncrementDownvote(commentUserVote1.getCommentId())
                )
                .map(voted -> new BooleanAndMessage(voted, "Toggled Vote"))
                .switchIfEmpty(Mono.error(new RuntimeException("Couldn't Toggle Vote")));
    }
}
