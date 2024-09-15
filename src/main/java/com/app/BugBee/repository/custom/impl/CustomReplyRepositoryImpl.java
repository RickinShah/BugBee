package com.app.BugBee.repository.custom.impl;

import com.app.BugBee.entity.Reply;
import com.app.BugBee.mapper.ReplyMapper;
import com.app.BugBee.repository.custom.CustomReplyRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class CustomReplyRepositoryImpl implements CustomReplyRepository {
    private final ReplyMapper mapper;
    private final DatabaseClient client;

    public CustomReplyRepositoryImpl(ReplyMapper mapper, DatabaseClient client) {
        this.mapper = mapper;
        this.client = client;
    }

    @Override
    public Mono<Reply> saveReply(Reply reply) {
        if (reply.getReplyId() != 0) {
            final String query = "WITH updated AS (UPDATE bugbee.replies SET content = :content, updated_at = :updatedAt, update_flag = :updateFlag" +
                    " WHERE reply_pid = :replyId AND user_id = :userId AND comment_id = :commentId RETURNING *)" +
                    " SELECT * FROM updated i LEFT JOIN bugbee.users u ON i.user_id = u.user_pid" +
                    " LEFT JOIN bugbee.comments c ON i.comment_id = c.comment_pid";

            return client.sql(query)
                    .bind("content", reply.getContent())
                    .bind("updatedAt", reply.getUpdatedAt())
                    .bind("updateFlag", reply.isUpdateFlag())
                    .bind("replyId", reply.getReplyId())
                    .bind("userId", reply.getUser().getUserId())
                    .bind("commentId", reply.getComment().getCommentId())
                    .map(mapper::apply)
                    .first();
        }
        final String query = " WITH inserted AS (INSERT INTO bugbee.replies (content, upvote_count, downvote_count, updated_at, update_flag, user_id, comment_id)" +
                " VALUES (:content, :upvoteCount, :downvoteCount, :updatedAt, :updateFlag, :userId, :commentId) RETURNING *)" +
                " SELECT * FROM inserted i LEFT JOIN bugbee.users u ON i.user_id = u.user_pid" +
                " LEFT JOIN bugbee.comments c ON i.comment_id = c.comment_pid";
        return client.sql(query)
                .bind("content", reply.getContent())
                .bind("upvoteCount", reply.getUpvoteCount())
                .bind("downvoteCount", reply.getDownvoteCount())
                .bind("updatedAt", reply.getUpdatedAt())
                .bind("updateFlag", reply.isUpdateFlag())
                .bind("userId", reply.getUser().getUserId())
                .bind("commentId", reply.getComment().getCommentId())
                .map(mapper::apply)
                .first();
    }

    @Override
    public Flux<Reply> findAllByCommentId(long commentId, Pageable pageable) {
        final String query = "SELECT * FROM bugbee.replies r" +
                " LEFT JOIN bugbee.users u ON r.user_id = u.user_pid" +
                " LEFT JOIN bugbee.comments c ON r.comment_id = c.comment_pid" +
                " WHERE r.comment_id = :commentId" +
                " ORDER BY r.reply_pid OFFSET :lastId LIMIT :size";
        return client.sql(query)
                .bind("lastId", pageable.getPageNumber())
                .bind("size", pageable.getPageSize())
                .bind("commentId", commentId)
                .map(mapper::apply)
                .all();
    }

    @Override
    public Mono<Long> deleteByIdAndUserIdAndCommentId(long replyId, long userId, long commentId) {
        final String query = "DELETE FROM bugbee.replies WHERE reply_pid = :replyId AND user_id = :userId AND comment_id = :commentId";

        return client.sql(query)
                .bind("replyId", replyId)
                .bind("userId", userId)
                .bind("commentId", commentId)
                .fetch()
                .rowsUpdated();
    }

    @Override
    public Mono<Reply> findByReplyId(long replyId) {
        final String query = "SELECT * FROM bugbee.replies r" +
                " LEFT JOIN bugbee.users u ON r.user_id = u.user_pid" +
                " LEFT JOIN bugbee.comments c ON r.comment_id = c.comment_pid" +
                " WHERE reply_pid = :replyId";
        return client.sql(query)
                .bind("replyId", replyId)
                .map(mapper::apply)
                .first();
    }
}
