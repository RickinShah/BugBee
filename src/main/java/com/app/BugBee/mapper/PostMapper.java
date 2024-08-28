package com.app.BugBee.mapper;

import com.app.BugBee.entity.Post;
import com.app.BugBee.enums.POST_TYPE;
import io.r2dbc.spi.Row;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.function.BiFunction;

@Component
public class PostMapper implements BiFunction<Row, Object, Post> {
    public Post apply(Row row, Object object) {

        return Post.builder().postId(row.get("post_pid", Long.class))
                .title(row.get("title", String.class))
                .content(row.get("content", String.class))
                .postType(POST_TYPE.valueOf(row.get("post_type", String.class)).name())
                .upvoteCount(row.get("upvote_count", Short.class))
                .downvoteCount(row.get("downvote_count", Short.class))
                .commentCount(row.get("comment_count", Short.class))
                .nsfwFlag(row.get("nsfw_flag", Boolean.class))
                .updatedAt(row.get("updated_at", LocalDate.class))
                .updateFlag(row.get("update_flag", Boolean.class))
                .user(new UserMapper().apply(row, object))
                .build();
    }
}
