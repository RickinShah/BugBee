package com.app.BugBee.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("bugbee.posts")
public class Post {
    @Id
    private long id;
    @Column("user_id")
    private long userId;
    private String title;
    private String post;
    @Column("type_of_post")
    private String typeOfPost;
    private short upvote;
    private short downvote;
    @Column("is_nsfw")
    private boolean isNsfw;
    private LocalDate date;
}
