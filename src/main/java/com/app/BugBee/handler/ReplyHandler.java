package com.app.BugBee.handler;

import com.app.BugBee.dto.BooleanAndMessage;
import com.app.BugBee.dto.CommentDto;
import com.app.BugBee.dto.ReplyDto;
import com.app.BugBee.dto.UserInfoDto;
import com.app.BugBee.entity.ReplyUserVote;
import com.app.BugBee.mapper.DtoEntityMapper;
import com.app.BugBee.repository.ReplyRepository;
import com.app.BugBee.repository.ReplyVoteRepository;
import com.app.BugBee.security.JwtTokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.Map;

@Service
@Slf4j
public class ReplyHandler {
    private final JwtTokenProvider tokenProvider;
    private final ReplyRepository repository;
    private final ReplyVoteRepository replyVoteRepository;

    public ReplyHandler(JwtTokenProvider tokenProvider, ReplyRepository repository, ReplyVoteRepository replyVoteRepository) {
        this.tokenProvider = tokenProvider;
        this.repository = repository;
        this.replyVoteRepository = replyVoteRepository;
    }

    public Mono<ServerResponse> getRepliesByCommentId(ServerRequest request) {
        final long userId = tokenProvider.getUsername(request.headers().header(HttpHeaders.AUTHORIZATION).getFirst().substring(7));
        final long commentId = Long.parseLong(request.pathVariable("commentId"));
        final int page = Integer.parseInt(request.queryParam("page").orElse("0"));
        final int size = Integer.parseInt(request.queryParam("size").orElse("5"));

        return ServerResponse.ok().body(BodyInserters.fromPublisher(
                repository.findAllByCommentId(commentId, PageRequest.of(page, size))
                        .map(DtoEntityMapper::replyToDto)
                        .flatMap(replyDto -> replyVoteRepository.findByReplyIdAndUserId(replyDto.getReplyId(), userId)
                                .doOnNext(replyUserVote -> {
                                    replyDto.setVoteStatus(replyUserVote.isVoteStatus());
                                    replyDto.setVotedFlag(true);
                                })
                                .map(replyUserVote -> replyDto)
                                .switchIfEmpty(Mono.fromCallable(() -> {
                                    replyDto.setVoteStatus(false);
                                    replyDto.setVotedFlag(false);
                                    return replyDto;
                                }))
                        ), ReplyDto.class
        ));
    }

    public Mono<ServerResponse> insertReplyByCommentId(ServerRequest request) {
        final long userId = tokenProvider.getUsername(request.headers().header(HttpHeaders.AUTHORIZATION).getFirst().substring(7));
        final long commentId = Long.parseLong(request.pathVariable("commentId"));
        final Mono<ReplyDto> replyDtoMono = request.bodyToMono(ReplyDto.class)
                .doOnNext(replyDto -> {
                    replyDto.setUser(UserInfoDto.builder().userId(userId).build());
                    replyDto.setComment(CommentDto.builder().commentId(commentId).build());
                    replyDto.setUpdatedAt(LocalDate.now());
                    replyDto.setUpdateFlag(false);
                });
        return replyDtoMono
                .map(DtoEntityMapper::dtoToReply)
                .flatMap(repository::saveReply)
                .flatMap(e -> ServerResponse.ok().body(BodyInserters.fromValue(
                    new BooleanAndMessage(true, "Reply Added!")
                )))
                .switchIfEmpty(ServerResponse.badRequest().body(BodyInserters.fromValue(
                    new BooleanAndMessage(false, "Reply Not Added!")
                )))
                .onErrorResume(Exception.class, e -> {
                    log.info(e.getMessage());
                    return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                            BodyInserters.fromValue(
                                    Map.of("error", "An unexpected error occurred. Please try again later!"))
                    );
                });
    }

    public Mono<ServerResponse> editReply(ServerRequest request) {
        final long userId = tokenProvider.getUsername(request.headers().header(HttpHeaders.AUTHORIZATION).getFirst().substring(7));
        final long commentId = Long.parseLong(request.pathVariable("commentId"));
        final long replyId = Long.parseLong(request.pathVariable("replyId"));
        final Mono<ReplyDto> replyDtoMono = request.bodyToMono(ReplyDto.class)
                .doOnNext(replyDto -> {
                    replyDto.setComment(CommentDto.builder().commentId(commentId).build());
                    replyDto.setUser(UserInfoDto.builder().userId(userId).build());
                    replyDto.setReplyId(replyId);
                    replyDto.setUpdatedAt(LocalDate.now());
                    replyDto.setUpdateFlag(true);
                });

        return replyDtoMono
                .map(DtoEntityMapper::dtoToReply)
                .flatMap(repository::saveReply)
                .flatMap(e -> ServerResponse.ok().body(BodyInserters.fromValue(
                    new BooleanAndMessage(true, "Reply Updated!")
                )))
                .switchIfEmpty(ServerResponse.badRequest().body(BodyInserters.fromValue(
                    new BooleanAndMessage(false, "Reply Not Updated!")
                )))
                .onErrorResume(Exception.class, e -> {
                    log.info(e.getMessage());
                    return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                            BodyInserters.fromValue(
                                    Map.of("error", "An unexpected error occurred. Please try again later!"))
                    );
                });
    }

    public Mono<ServerResponse> deleteReply(ServerRequest request) {
        final long userId = tokenProvider.getUsername(request.headers().header(HttpHeaders.AUTHORIZATION).getFirst().substring(7));
        final long commentId = Long.parseLong(request.pathVariable("commentId"));
        final long replyId = Long.parseLong(request.pathVariable("replyId"));
        return repository
                .deleteByIdAndUserIdAndCommentId(replyId, userId, commentId)
                .flatMap(e -> ServerResponse.ok().body(BodyInserters.fromValue(
                    new BooleanAndMessage(true, "Reply Deleted!")
                )))
                .switchIfEmpty(ServerResponse.badRequest().body(BodyInserters.fromValue(
                    new BooleanAndMessage(false, "Reply Not Deleted!")
                )))
                .onErrorResume(Exception.class, e -> {
                    log.info(e.getMessage());
                    return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                            BodyInserters.fromValue(
                                    Map.of("error", "An unexpected error occurred. Please try again later!"))
                    );
                });
    }

    public Mono<ServerResponse> voteReply(ServerRequest request) {
        final long userId = tokenProvider.getUsername(request.headers().header(HttpHeaders.AUTHORIZATION).getFirst().substring(7));
        final long replyId = Long.parseLong(request.pathVariable("replyId"));
        final Mono<ReplyUserVote> replyUserVoteMono = request.bodyToMono(ReplyUserVote.class)
                .doOnNext(replyUserVote -> {
                    replyUserVote.setReplyId(replyId);
                    replyUserVote.setUserId(userId);
                });
        return replyUserVoteMono
                .flatMap(replyUserVote ->
                        replyVoteRepository.existsByReplyIdAndUserId(
                                replyUserVote.getReplyId(), replyUserVote.getUserId()
                        )
                                .flatMap(exists -> exists ?
                                        upvoteOrDownvoteIfAlreadyExists(replyUserVote) :
                                        upvoteOrDownvoteIfNotExists(replyUserVote)
                                        )
                )
                .flatMap(e -> ServerResponse.ok().body(BodyInserters.fromValue(
                        new BooleanAndMessage(e.isSuccess(), e.getMessage())
                )))
                .onErrorResume(Exception.class, e -> {
                    log.info(e.getMessage());
                    return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                            BodyInserters.fromValue(
                                    Map.of("error", "An unexpected error occurred. Please try again later!"))
                    );
                });
    }

    // Internal Methods Below

    private Mono<BooleanAndMessage> upvoteOrDownvoteIfNotExists(ReplyUserVote replyUserVote) {
        return replyVoteRepository.save(replyUserVote)
                .flatMap(replyUserVote1 -> replyUserVote1.isVoteStatus() ?
                        repository.incrementUpvoteByReplyId(replyUserVote1.getReplyId()) :
                        repository.incrementDownvoteByReplyId(replyUserVote1.getReplyId())
                )
                .map(voted -> new BooleanAndMessage(voted, "Upvoted/Downvoted"))
                .switchIfEmpty(Mono.error(new RuntimeException("Couldn't Upvote/Downvote")));
    }

    private Mono<BooleanAndMessage> upvoteOrDownvoteIfAlreadyExists(ReplyUserVote replyUserVote) {
        return Mono.fromCallable(() -> replyUserVote)
                .flatMap(replyUserVote1 -> replyVoteRepository.findById(replyUserVote.getReplyId())
                        .doOnNext(replyUserVote2 -> replyUserVote1.setVoteId(replyUserVote2.getVoteId()))
                        .flatMap(replyUserVote2 -> replyUserVote1.isVoteStatus() == replyUserVote2.isVoteStatus() ?
                                deleteVote(replyUserVote1) :
                                toggleVote(replyUserVote1)
                        )
                )
                .map(booleanAndMessage -> booleanAndMessage);
    }

    private Mono<BooleanAndMessage> deleteVote(ReplyUserVote replyUserVote) {
        return replyVoteRepository.delete(replyUserVote)
                .then(replyUserVote.isVoteStatus() ?
                        repository.decrementUpvoteByReplyId(replyUserVote.getReplyId()) :
                        repository.decrementDownvoteByReplyId(replyUserVote.getReplyId())
                )
                .map(unvoted -> new BooleanAndMessage(unvoted, "Removed Vote"))
                .switchIfEmpty(Mono.error(new RuntimeException("Couldn't Remove Vote")));
    }

    private Mono<BooleanAndMessage> toggleVote(ReplyUserVote replyUserVote) {
        return replyVoteRepository.save(replyUserVote)
                .flatMap(replyUserVote1 -> replyUserVote1.isVoteStatus() ?
                        repository.incrementUpvoteAndDecrementDownvote(replyUserVote1.getReplyId()) :
                        repository.decrementUpvoteAndIncrementDownvote(replyUserVote1.getReplyId())
                )
                .map(voted -> new BooleanAndMessage(voted, "Toggled Vote"))
                .switchIfEmpty(Mono.error(new RuntimeException("Couldn't Toggle Vote")));
    }

}
