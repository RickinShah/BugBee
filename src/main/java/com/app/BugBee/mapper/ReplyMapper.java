package com.app.BugBee.mapper;

import com.app.BugBee.entity.Reply;
import io.r2dbc.spi.Row;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.function.BiFunction;

@Component
public class ReplyMapper implements BiFunction<Row, Object, Reply> {

    @Override
    public Reply apply(Row row, Object o) {
        return Reply.builder()
                .replyId(row.get("reply_pid", Long.class))
                .content(row.get("content", String.class))
                .upvoteCount(row.get("upvote_count", Integer.class))
                .downvoteCount(row.get("downvote_count", Integer.class))
                .updatedAt(row.get("updated_at", LocalDate.class))
                .updateFlag(row.get("update_flag", Boolean.class))
                .user(new UserMapper().apply(row, o))
                .comment(new CommentMapper().apply(row, o))
                .build();
    }
}
