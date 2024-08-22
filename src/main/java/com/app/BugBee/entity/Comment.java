package com.app.BugBee.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.sql.Timestamp;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Table("comments")
public class Comment {
    @Id
    private UUID id;
    @Column("post_id")
    private UUID postId;
    @Column("user_id")
    private UUID userId;
    private String comment;
    private short upvote;
    private short downvote;
    private Timestamp time;
}
