package com.app.BugBee.mapper;

import com.app.BugBee.entity.Post;
import com.app.BugBee.entity.User;
import com.app.BugBee.enums.TYPE_OF_POST;
import io.r2dbc.spi.Row;

import java.time.LocalDate;
import java.util.function.BiFunction;

public class PostUserMapper implements BiFunction<Row, Object, Post> {
    public Post apply(Row row, Object object) {
            long postId = row.get("post_id", Long.class);
            String title = row.get("title", String.class);
            String content = row.get("content", String.class);
            TYPE_OF_POST typeOfPost = TYPE_OF_POST.valueOf(row.get("type_of_post", String.class));
            short upvote = row.get("upvote", Short.class);
            short downvote = (row.get("downvote", Short.class));
            short totalComments = row.get("total_comments", Short.class);
            boolean isNsfw = row.get("nsfw", Boolean.class);
            LocalDate date = row.get("date", LocalDate.class);

            long userId = row.get("user_id", Long.class);
            String username = row.get("username", String.class);
            String email = row.get("email", String.class);
            String name = row.get("name", String.class);
            String password = row.get("password", String.class);
            String roles = row.get("roles", String.class);
            boolean showNsfw = row.get("show_nsfw", Boolean.class);


            User user = new User(userId, username, email, name, password, roles, showNsfw);
            Post post = new Post(
                    postId, title, content, typeOfPost.name(), upvote, downvote, totalComments, isNsfw, date, user
            );

        return post;

    }
}
