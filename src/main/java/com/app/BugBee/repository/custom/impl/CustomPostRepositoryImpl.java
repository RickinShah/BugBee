package com.app.BugBee.repository.custom.impl;

import com.app.BugBee.entity.Post;
import com.app.BugBee.mapper.PostMapper;
import com.app.BugBee.repository.custom.CustomPostRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
public class CustomPostRepositoryImpl implements CustomPostRepository {

    private final PostMapper postMapper;
    private final DatabaseClient client;

    public CustomPostRepositoryImpl(PostMapper postMapper, DatabaseClient databaseClient) {
        this.postMapper = postMapper;
        this.client = databaseClient;
    }

    public Flux<Post> findAll(Pageable pageable) {
        String query = "SELECT * FROM bugbee.posts p" +
                " LEFT JOIN bugbee.users u ON p.user_id = u.user_pid" +
                " LEFT JOIN bugbee.resources r ON p.post_pid = r.post_pid" +
                " ORDER BY p.post_pid DESC OFFSET :lastId LIMIT :size";

        return client.sql(query)
                .bind("size", pageable.getPageSize())
                .bind("lastId", pageable.getPageNumber())
                .map(postMapper::apply)
                .all();
    }

    @Override
    public Mono<Post> savePost(Post post) {
//        log.info(post.toString());
//        log.info("savePost called");
        if (post.getPostId() != 0) {
//            log.info("update");
            String query = "WITH updated AS (UPDATE bugbee.posts SET" +
                    " content = :content, title = :title, updated_at = :updatedAt, update_flag = true" +
                    " WHERE post_pid = :postId RETURNING *)" +
                    " SELECT * FROM updated i LEFT JOIN bugbee.users u ON i.user_id = u.user_pid" +
                    " LEFT JOIN bugbee.resources r ON i.post_pid = r.post_pid";

            return client.sql(query)
                    .bind("content", post.getContent())
                    .bind("title", post.getTitle())
                    .bind("postId", post.getPostId())
                    .bind("updatedAt", post.getUpdatedAt())
                    .map(postMapper::apply)
                    .first();

        }
        String query =
                "WITH inserted AS (INSERT INTO bugbee.posts(title, content, post_type, upvote_count, downvote_count," +
                        "comment_count, updated_at, update_flag, user_id)" +
                        " VALUES (:title, :content, :postType, :upvoteCount, :downvoteCount," +
                        ":commentCount, :updatedAt, false, :userId) RETURNING *)" +
                        " SELECT * FROM inserted i LEFT JOIN bugbee.users u ON i.user_id = u.user_pid" +
                        " LEFT JOIN bugbee.resources r ON i.post_pid = r.post_pid";

        return client.sql(query)
                .bind("title", post.getTitle())
                .bind("content", post.getContent())
                .bind("postType", post.getPostType())
                .bind("upvoteCount", post.getUpvoteCount())
                .bind("downvoteCount", post.getDownvoteCount())
                .bind("commentCount", post.getCommentCount())
                .bind("updatedAt", post.getUpdatedAt())
                .bind("userId", post.getUser().getUserId())
                .map(postMapper::apply)
                .first();
//        log.info("savePost completed");

    }

    @Override
    public Mono<Post> findByPostId(Long postId) {
        final String query = "SELECT * FROM bugbee.posts p" +
                " LEFT OUTER JOIN bugbee.users u ON p.user_id = u.user_pid" +
                " LEFT OUTER JOIN bugbee.resources r ON p.post_pid = r.post_pid" +
                " WHERE p.post_pid = :postId";

        return client.sql(query)
                .bind("postId", postId)
                .map(postMapper::apply)
                .first();
    }

    @Override
    public Mono<Long> deleteByPostIdAndUserId(Long postId, Long userId) {
        final String query = "DELETE FROM bugbee.posts WHERE post_pid = :postId AND user_id = :userId";

        return client.sql(query)
                .bind("userId", userId)
                .bind("postId", postId)
                .fetch()
                .rowsUpdated();
    }
}
