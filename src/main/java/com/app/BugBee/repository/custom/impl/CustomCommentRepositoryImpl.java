package com.app.BugBee.repository.custom.impl;

import com.app.BugBee.entity.Comment;
import com.app.BugBee.mapper.CommentMapper;
import com.app.BugBee.repository.custom.CustomCommentRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class CustomCommentRepositoryImpl implements CustomCommentRepository {
    private final CommentMapper mapper;
    private final DatabaseClient client;

    public CustomCommentRepositoryImpl(CommentMapper mapper, DatabaseClient client) {
        this.mapper = mapper;
        this.client = client;
    }

    @Override
    public Mono<Comment> saveComment(Comment comment) {
        if (comment.getCommentId() != 0) {
            final String query = "WITH updated AS (UPDATE bugbee.comments SET" +
                    " content = :content, updated_at = :updatedAt, update_flag = true" +
                    " WHERE comment_pid = :commentId AND post_id = :postId AND user_id = :userId RETURNING *)" +
                    " SELECT * FROM updated i LEFT JOIN bugbee.users u ON i.user_id = u.user_pid" +
                    " LEFT JOIN bugbee.posts p ON i.post_id = p.post_pid" +
                    " LEFT JOIN bugbee.resources r ON r.post_pid = p.post_pid";

            return client.sql(query)
                    .bind("content", comment.getContent())
                    .bind("updatedAt", comment.getUpdatedAt())
                    .bind("commentId", comment.getCommentId())
                    .bind("userId", comment.getUser().getUserId())
                    .bind("postId", comment.getPost().getPostId())
                    .map(mapper::apply)
                    .first();
        }
        final String query = "WITH inserted AS (INSERT INTO bugbee.comments(content, upvote_count, downvote_count, reply_count, updated_at, update_flag, user_id, post_id)" +
                " VALUES (:content, :upvoteCount, :downvoteCount, :replyCount, :updatedAt, :updateFlag, :userId, :postId) RETURNING *)" +
                " SELECT * FROM inserted i LEFT JOIN bugbee.users u ON i.user_id = u.user_pid" +
                " LEFT JOIN bugbee.posts p ON i.post_id = p.post_pid" +
                " LEFT JOIN bugbee.resources r ON r.post_pid = p.post_pid";

        return client.sql(query)
                .bind("content", comment.getContent())
                .bind("upvoteCount", comment.getUpvoteCount())
                .bind("downvoteCount", comment.getDownvoteCount())
                .bind("replyCount", comment.getReplyCount())
                .bind("updatedAt", comment.getUpdatedAt())
                .bind("updateFlag", comment.isUpdateFlag())
                .bind("userId", comment.getUser().getUserId())
                .bind("postId", comment.getPost().getPostId())
                .map(mapper::apply)
                .first();
    }

    @Override
    public Mono<Long> deleteByIdAndUserIdAndPostId(long commentId, long userId, long postId) {
        final String query = "DELETE FROM bugbee.comments WHERE comment_pid = :commentId AND user_id = :userId AND post_id = :postId";
        return client.sql(query)
                .bind("commentId", commentId)
                .bind("userId", userId)
                .bind("postId", postId)
                .fetch()
                .rowsUpdated();
    }

    @Override
    public Flux<Comment> findAllByPostId(long postId, Pageable pageable) {
        final String query = "SELECT * FROM bugbee.comments c" +
                " LEFT JOIN bugbee.users u ON c.user_id = u.user_pid" +
                " LEFT JOIN bugbee.posts p ON c.post_id = p.post_pid" +
                " LEFT JOIN bugbee.resources r ON r.post_pid = p.post_pid" +
                " WHERE c.post_id = :postId" +
                " ORDER BY c.comment_pid OFFSET :lastId LIMIT :size";
        return client.sql(query)
                .bind("lastId", pageable.getPageNumber())
                .bind("size", pageable.getPageSize())
                .bind("postId", postId)
                .map(mapper::apply)
                .all();
    }

    @Override
    public Mono<Comment> findByCommentId(long commentId) {
        final String query = "SELECT * FROM bugbee.comments c" +
                " LEFT JOIN bugbee.users u ON c.user_id = u.user_pid" +
                " LEFT JOIN bugbee.posts p ON c.post_id = p.post_pid" +
                " WHERE comment_pid = :commentId";
        return client.sql(query)
                .bind("commentId", commentId)
                .map(mapper::apply)
                .first();
    }


}
