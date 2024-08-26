package com.app.BugBee.repository.custom.impl;

import com.app.BugBee.entity.Post;
import com.app.BugBee.mapper.PostUserMapper;
import com.app.BugBee.repository.custom.CustomPostRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
public class CustomPostRepositoryImpl implements CustomPostRepository {

    private final DatabaseClient databaseClient;

    public CustomPostRepositoryImpl(DatabaseClient databaseClient) {
        this.databaseClient = databaseClient;
    }

    public Flux<Post> findAll(Pageable pageable) {
        String query = "SELECT * FROM bugbee.posts p " +
                "LEFT JOIN bugbee.users u " +
                "ON p.user_id = u.user_id ORDER BY p.post_id DESC OFFSET :lastId LIMIT :size";

        PostUserMapper postUserMapper = new PostUserMapper();

        Flux<Post> result = databaseClient.sql(query)
                .bind("size", pageable.getPageSize())
                .bind("lastId", pageable.getPageNumber())
                .map(postUserMapper::apply)
                .all();

        return result;
    }

    @Override
    public Mono<Long> savePost(Post post) {
//        log.info("savePost called");
        String query =
                "INSERT INTO bugbee.posts(title, content, type_of_post," +
                        "upvote, downvote, total_comments, nsfw, date, user_id)" +
                " VALUES (:title, :content, :typeOfPost," +
                        ":upvote, :downvote, :totalComments," +
                        ":nsfw, :date, :userId)";

        Mono<Long> result = databaseClient.sql(query)
                .bind("title", post.getTitle())
                .bind("content", post.getContent())
                .bind("typeOfPost", post.getTypeOfPost())
                .bind("upvote", post.getUpvote())
                .bind("downvote", post.getDownvote())
                .bind("totalComments", post.getTotalComments())
                .bind("nsfw", post.isNsfw())
                .bind("date", post.getDate())
                .bind("userId", post.getUser().getUserId())
                .fetch()
                .rowsUpdated();
//        log.info("savePost completed");

        return result;
    }
}
