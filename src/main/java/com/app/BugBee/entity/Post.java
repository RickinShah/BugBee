package com.app.BugBee.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("bugbee.posts")
public class Post {
    @Id
    @Column("post_id")
    private long postId;
    private String title;
    private String content;
    @Column("type_of_post")
    private String typeOfPost;
    private short upvote;
    private short downvote;
    @Column("total_comments")
    private short totalComments;
    @Column("nsfw")
    private boolean nsfw;
    private LocalDate date;
    private User user;
}
