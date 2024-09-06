package com.app.BugBee.mapper;

import com.app.BugBee.entity.Comment;
import io.r2dbc.spi.Row;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.function.BiFunction;

@Component
public class CommentMapper implements BiFunction<Row, Object, Comment> {

    @Override
    public Comment apply(Row row, Object o) {
        return Comment.builder()
                .commentId(row.get("comment_pid", Long.class))
                .content(row.get("content", String.class))
                .upvoteCount(row.get("upvote_count", Integer.class))
                .downvoteCount(row.get("downvote_count", Integer.class))
                .replyCount(row.get("reply_count", Integer.class))
                .updatedAt(row.get("updated_at", LocalDate.class))
                .updateFlag(row.get("update_flag", Boolean.class))
                .post(new PostMapper().apply(row, o))
                .user(new UserMapper().apply(row, o))
                .build();
    }
}
