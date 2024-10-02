package com.app.BugBee.handler;

import com.app.BugBee.dto.BooleanAndMessage;
import com.app.BugBee.dto.PostDto;
import com.app.BugBee.entity.Post;
import com.app.BugBee.entity.PostUserVote;
import com.app.BugBee.entity.Resource;
import com.app.BugBee.enums.FILE_FORMATS;
import com.app.BugBee.enums.POST_TYPE;
import com.app.BugBee.mapper.DtoEntityMapper;
import com.app.BugBee.repository.PostRepository;
import com.app.BugBee.repository.PostVoteRepository;
import com.app.BugBee.repository.ResourceRepository;
import com.app.BugBee.security.JwtTokenProvider;
import com.app.BugBee.utils.FileEncryptionUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.FormFieldPart;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyExtractors;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Path;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.Base64;
import java.util.Map;

@Service
@Slf4j
public class PostHandler {
    private final PostRepository repository;

    private final JwtTokenProvider tokenProvider;

    private final PostVoteRepository postVoteRepository;

    private final ResourceRepository resourceRepository;

    private final NsfwHandler nsfwHandler;

    public PostHandler(PostRepository repository, JwtTokenProvider tokenProvider, PostVoteRepository postVoteRepository, ResourceRepository resourceRepository, NsfwHandler nsfwHandler) {
        this.repository = repository;
        this.tokenProvider = tokenProvider;
        this.postVoteRepository = postVoteRepository;
        this.resourceRepository = resourceRepository;
        this.nsfwHandler = nsfwHandler;
    }

    public Mono<ServerResponse> insertPost(ServerRequest request) {
        final String token = tokenProvider.getToken(request);
        final long userId = tokenProvider.getUsername(token);
        return request.body(BodyExtractors.toMultipartData())
                .map(MultiValueMap::toSingleValueMap)
                .flatMap(partMap ->
                        Mono.just((FormFieldPart) partMap.get("post"))
                                .map(postForm -> DtoEntityMapper.dtoToPost(
                                        new PostDto(JsonParserFactory
                                                .getJsonParser()
                                                .parseMap(postForm.value()), userId)
                                ))
                                .doOnNext(post -> post.setPostType(
                                        (getPostType(((FilePart) partMap.get("resource")).filename())).name()))
                                .flatMap(repository::savePost)
                                .doOnNext(post -> log.info("Post saved: {}", post.getPostId()))
                                .flatMap(post -> {
                                    FilePart resource = (FilePart) partMap.get("resource");
                                    return saveFileToPath(
                                            POST_TYPE.valueOf(post.getPostType()).getValues()[0],
                                            resource,
                                            post,
                                            token
                                    );
                                }))
                .flatMap(e -> ServerResponse.ok().body(BodyInserters.fromValue(
                        new BooleanAndMessage(true, "Posted Successfully!")
                )));
    }

    public Mono<ServerResponse> downloadFile(ServerRequest request) {
        final String fileFormat = request.pathVariable("fileFormat");
        final long postId = Long.parseLong(request.pathVariable("postId"));

        return Mono.fromCallable(() -> getPostType(postId + "." + fileFormat).getValues()[0] +
                        "/" + postId + "." + fileFormat
                )
                .flatMap(path -> decryptFile(path, postId))
                .doOnNext(path -> log.info("File Decrypted: {}.{}", postId, fileFormat))
                .flatMap(e -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .header(HttpHeaders.CONTENT_DISPOSITION,
                                "attachment; filename=\"" + postId + "." + fileFormat + "\""
                        )
                        .body(BodyInserters.fromValue(e.get("file")))
                )
                .onErrorResume(RuntimeException.class, e -> {
                    log.info(e.getMessage());
                    return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                            BodyInserters.fromValue(
                                    Map.of("error", "An unexpected error occurred. Please try again later!"))
                    );
                });
    }

    public Mono<ServerResponse> getFile(ServerRequest request) {
        final long postId = Long.parseLong(request.pathVariable("postId"));
        final String fileFormat = request.pathVariable("fileFormat");

        return Mono.fromCallable(() -> getPostType(postId + "." + fileFormat).getValues()[0] +
                        "/" + postId + "." + fileFormat
                )
                .flatMap(path -> decryptFile(path, postId))
                .doOnNext(path -> log.info("File Decrypted: {}.{}", postId, fileFormat))
                .flatMap(e -> ServerResponse.ok()
                        .contentType(MediaType.parseMediaType(e.get("mediaType").toString()))
                        .body(BodyInserters.fromValue(e.get("file")))
                )
                .onErrorResume(RuntimeException.class, e -> {
                    log.info(e.getMessage());
                    return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                            BodyInserters.fromValue(
                                    Map.of("error", "An unexpected error occurred. Please try again later!"))
                    );
                });
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
                .doOnNext(post -> log.info("Post updated: {}", post.getPostId()))
                .flatMap(post -> ServerResponse.ok().body(BodyInserters.fromValue(
                        new BooleanAndMessage(false, "Nothing to delete!")
                )))
                .switchIfEmpty(ServerResponse.ok().body(BodyInserters.fromValue(
                        new BooleanAndMessage(true, "Post updated successfully!")
                )));
    }

    public Mono<ServerResponse> deletePost(ServerRequest request) {
        final long userId = tokenProvider.getUsername(tokenProvider.getToken(request));
        final long postId = Long.parseLong(request.pathVariable("postId"));

        return repository.deleteByPostIdAndUserId(postId, userId)
                .then(ServerResponse.noContent().build());
    }

    public Mono<ServerResponse> votePost(ServerRequest request) {
        final long userId = tokenProvider.getUsername(tokenProvider.getToken(request));
        final long postId = Long.parseLong(request.pathVariable("postId"));
        final Mono<PostUserVote> postUserVoteMono = request.bodyToMono(PostUserVote.class)
                .doOnNext(postUserVote -> {
                    postUserVote.setPostId(postId);
                    postUserVote.setUserId(userId);
                });

        return postUserVoteMono
                .flatMap(postUserVote -> postVoteRepository.findByUserIdAndPostId(
                                        postUserVote.getUserId(), postUserVote.getPostId()
                                )
                                .defaultIfEmpty(new PostUserVote())
                                .filter(vote -> vote.getPostId() != 0)
                                .flatMap(vote -> upvoteOrDownvoteIfAlreadyExists(vote, postUserVote))
                                .switchIfEmpty(upvoteOrDownvoteIfNotExists(postUserVote))
                )
                .flatMap(booleanAndMessage -> booleanAndMessage.isSuccess() ?
                        ServerResponse.ok().build() :
                        ServerResponse.badRequest().body(BodyInserters.fromValue(booleanAndMessage))
                );

    }

    public Mono<ServerResponse> getNextPosts(ServerRequest request) {
        final long userId = tokenProvider.getUsername(tokenProvider.getToken(request));
        final int offset = Integer.parseInt(request.queryParam("offset").orElse("0"));
        final int size = Integer.parseInt(request.queryParam("size").orElse("0"));
        return ServerResponse.ok().body(BodyInserters.fromPublisher(
                repository.findAll(PageRequest.of(offset, size))
                        .map(DtoEntityMapper::postToDto)
                        .doOnNext(postDto -> postDto
                                .setPostType(POST_TYPE.valueOf(postDto.getPostType()).getValues()[1] +
                                        File.separator + postDto.getPostId() +
                                        FILE_FORMATS.valueOf(postDto.getResource().getFileFormat()).value)
                        )
                        .flatMap(postDto ->
                                postVoteRepository.findByUserIdAndPostId(
                                                userId, postDto.getPostId()
                                        )
                                        .doOnNext(postUserVote -> {
                                            postDto.setVoteStatus(postUserVote.isVoteStatus());
                                            postDto.setVotedFlag(true);
                                        })
                                        .doOnNext(postUserVote -> postDto
                                                .setPostType(POST_TYPE.valueOf(postDto.getPostType()).getValues()[1]))
                                        .map(postUserVote -> postDto)
                                        .switchIfEmpty(Mono.just(postDto))
                        )
                , PostDto.class
        ));
    }

    public Mono<ServerResponse> getSinglePost(ServerRequest request) {
        final long userId = tokenProvider.getUsername(tokenProvider.getToken(request));
        final long postId = Long.parseLong(request.pathVariable("postId"));

        return repository.findByPostId(postId)
                .map(DtoEntityMapper::postToDto)
                .doOnNext(postDto -> postDto
                        .setPostType(POST_TYPE.valueOf(postDto.getPostType()).getValues()[1] +
                                "/" + postDto.getPostId() +
                                FILE_FORMATS.valueOf(postDto.getResource().getFileFormat()).value)
                )
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
        return postVoteRepository.save(postUserVote)
                .flatMap(postUserVote1 -> postUserVote1.isVoteStatus() ?
                        repository.incrementUpvoteByPostId(postUserVote1.getPostId()) :
                        repository.incrementDownvoteByPostId(postUserVote1.getPostId())
                )
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
        if (postName.matches(".*\\.(png|jpg|jpeg)$"))
            return POST_TYPE.IMAGE;
        else if (postName.matches(".*\\.(mp4|webm)$"))
            return POST_TYPE.VIDEO;
        else if (postName.matches(".*\\.(mp3|wav|ogg)$"))
            return POST_TYPE.AUDIO;
        else if (postName.matches(".*\\.(pdf)$"))
            return POST_TYPE.DOCUMENT;
        return POST_TYPE.QUESTION;
    }

    private Mono<Long> saveFileToPath(String path, FilePart resource, Post post, String token) {
        String fileFormat = resource.filename().substring(resource.filename().lastIndexOf('.'));
        if (!new File(path).exists()) {
            new File(path).mkdirs();
        }
        Path filePath = Path.of(new File(path + File.separator + post.getPostId() + fileFormat).getAbsolutePath());

        return DataBufferUtils.join(resource.content())
                .flatMap(dataBuffer -> {
                    byte[] fileBytes = new byte[dataBuffer.readableByteCount()];
                    dataBuffer.read(fileBytes);
                    DataBufferUtils.release(dataBuffer);
                    try {
                        SecretKey secretKey = FileEncryptionUtils.generateKey();
                        byte[] iv = new byte[16];
                        new SecureRandom().nextBytes(iv);

                        byte[] encryptedContent = FileEncryptionUtils.encrypt(fileBytes, secretKey, iv);

                        return Mono.fromCallable(() -> {
                                    FileCopyUtils.copy(encryptedContent, new File(filePath.toString()).getAbsoluteFile());
                                    return 1;
                                })
                                .map(e -> Resource.builder()
                                        .secretKey(Base64.getEncoder().encodeToString(secretKey.getEncoded()))
                                        .postId(post.getPostId())
                                        .fileFormat(fileFormat).nsfwFlag(false)
                                        .iv(iv)
                                        .build()
                                )
                                .doOnNext(resource1 -> resource1.setFileFormat(
                                        FILE_FORMATS.valueOf(fileFormat.substring(1).toUpperCase()).name()
                                        )
                                )
                                .flatMap(resource1 -> resourceRepository.save(resource1)
                                        .flatMap(e -> {
                                            if(resource1.getFileFormat().equals(FILE_FORMATS.JPG.name()) || resource1.getFileFormat().equals(FILE_FORMATS.PNG.name()) || resource1.getFileFormat().equals(FILE_FORMATS.JPEG.name())) {
                                                return nsfwHandler.checkIfNsfw("http://spring/api/posts/get/" + post.getPostId() +  FILE_FORMATS.valueOf(resource1.getFileFormat()).value, token)
                                                        .map(isNsfw -> {
                                                            resource1.setNsfwFlag(isNsfw);
                                                            return resource1;
                                                        });
                                            }
                                            return Mono.just(resource1);
                                        })
                                )
                                .filter(Resource::isNsfwFlag)
                                .flatMap(resourceRepository::save)
                                .defaultIfEmpty(1L);
                    } catch (Exception e) {
                        log.info(e.getMessage());
                        return Mono.empty();

                    }

                });
    }

    private Mono<Map<String, Serializable>> decryptFile(String path, long postId) {
        File file = new File(path);
        try {
            byte[] encryptedContent = FileCopyUtils.copyToByteArray(file.getAbsoluteFile());
            return resourceRepository.findById(postId)
                    .map(resource -> {
                        byte[] decodedKey = Base64.getDecoder().decode(resource.getSecretKey());
                        SecretKey key = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
                        return FileEncryptionUtils.decrypt(encryptedContent, key, resource.getIv());
                    })
                    .map(bytes -> Map.of(
                            "file", bytes,
                            "mediaType", new Tika().detect(bytes)));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
