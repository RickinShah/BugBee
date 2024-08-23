package com.app.BugBee.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Table("bugbee.comments")
public class Comment {
    @Id
    private long id;
    @Column("post_id")
    private long postId;
    @Column("user_id")
    private long userId;
    private String comment;
    private short upvote;
    private short downvote;
    private LocalDate date;
}
