package com.app.BugBee.handler;

import com.app.BugBee.dto.BooleanAndMessage;
import com.app.BugBee.dto.PostDto;
import com.app.BugBee.entity.PostUserVote;
import com.app.BugBee.entity.Resource;
import com.app.BugBee.enums.FILE_FORMATS;
import com.app.BugBee.enums.POST_TYPE;
import com.app.BugBee.mapper.DtoEntityMapper;
import com.app.BugBee.repository.PostRepository;
import com.app.BugBee.repository.PostVoteRepository;
import com.app.BugBee.repository.ResourceRepository;
import com.app.BugBee.security.JwtTokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.FormFieldPart;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyExtractors;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;

@Service
@Slf4j
public class PostHandler {
    private final PostRepository repository;

    private final JwtTokenProvider tokenProvider;

    private final PostVoteRepository postVoteRepository;

    private final ResourceRepository resourceRepository;

    public PostHandler(PostRepository repository, JwtTokenProvider tokenProvider, PostVoteRepository postVoteRepository, ResourceRepository resourceRepository) {
        this.repository = repository;
        this.tokenProvider = tokenProvider;
        this.postVoteRepository = postVoteRepository;
        this.resourceRepository = resourceRepository;
    }

    public Mono<ServerResponse> insertPost(ServerRequest request) {
//        final long userId = tokenProvider.getUsername(request.headers().header(HttpHeaders.AUTHORIZATION).getFirst().substring(7));
        final long userId = 3448660623615857777L;
        return request.body(BodyExtractors.toMultipartData())
                .map(MultiValueMap::toSingleValueMap)
                .flatMap(partMap ->
                        Mono.just((FormFieldPart) partMap.get("post"))
                                .map(postForm -> DtoEntityMapper.dtoToPost(
                                        new PostDto(JsonParserFactory
                                                .getJsonParser()
                                                .parseMap(postForm.value()), userId)
                                ))
                                .doOnNext(post -> post.setPostType((getPostType(((FilePart) partMap.get("resource")).filename())).name()))
                                .flatMap(repository::savePost)
                                .flatMap(post -> {
                                    FilePart resource = (FilePart) partMap.get("resource");
                                    return saveFileToPath(
                                            POST_TYPE.valueOf(post.getPostType()).getValues()[0],
                                            resource,
                                            post.getPostId())
                                            .map(fileFormat -> Resource.builder()
                                                    .fileFormat(fileFormat)
                                                    .postId(post.getPostId())
                                                    .nsfwFlag(false)
                                                    .build()
                                            );
                                }))
                .flatMap(resourceRepository::save)
                .flatMap(e -> ServerResponse.ok().body(BodyInserters.fromValue(
                        new BooleanAndMessage(true, "Posted Successfully!")
                )));
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
                .flatMap(post -> ServerResponse.ok().body(BodyInserters.fromValue(
                        new BooleanAndMessage(false, "Nothing to delete!")
                )))
                .switchIfEmpty(ServerResponse.ok().body(BodyInserters.fromValue(
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
                        .doOnNext(postDto -> postDto.setPostType(POST_TYPE.valueOf(postDto.getPostType()).getValues()[1] + "/" + postDto.getPostId() + FILE_FORMATS.valueOf(postDto.getResource().getFileFormat()).value))
                        .flatMap(postDto ->
                                postVoteRepository.findByUserIdAndPostId(
                                                userId, postDto.getPostId()
                                        )
                                        .doOnNext(postUserVote -> {
                                            postDto.setVoteStatus(postUserVote.isVoteStatus());
                                            postDto.setVotedFlag(true);
                                        })
                                        .doOnNext(postUserVote -> postDto.setPostType(POST_TYPE.valueOf(postDto.getPostType()).getValues()[1]))
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
                .doOnNext(postDto -> postDto.setPostType(POST_TYPE.valueOf(postDto.getPostType()).getValues()[1] + "/" + postDto.getPostId() + FILE_FORMATS.valueOf(postDto.getResource().getFileFormat()).value))
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

    private POST_TYPE getPostType(String postName) {
        postName = postName.toLowerCase();
        if (postName.matches(".*\\.(png|jpg)$"))
            return POST_TYPE.IMAGE;
        else if (postName.matches(".*\\.(mp4|webm)$"))
            return POST_TYPE.VIDEO;
        else if (postName.matches(".*\\.(mp3|wav|ogg)$"))
            return POST_TYPE.AUDIO;
        else if (postName.matches(".*\\.(pdf)$"))
            return POST_TYPE.DOCUMENT;
        return POST_TYPE.QUESTION;
    }

    private Mono<String> saveFileToPath(String path, FilePart resource, long postId) {
        String fileFormat = resource.filename().substring(resource.filename().lastIndexOf('.'));
        if (!new File(path).exists()) {
            new File(path).mkdirs();
        }
        Path filePath = Path.of(new File(path + "/" + postId + fileFormat).getAbsolutePath());
        return DataBufferUtils.write(resource.content(), filePath, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)
//                .doOnSuccess(e -> log.info("File saved successfully to {}", filePath.toAbsolutePath()))
//                .doOnError(e -> log.error("Failed to save file", e))
                .then(Mono.just(fileFormat.substring(1).toUpperCase()));
    }
}