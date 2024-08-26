package com.app.BugBee.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Table("bugbee.comments")
public class Comment {
    @Id
    @Column("comment_id")
    private long commentId;
    private String comment;
    private short upvote;
    private short downvote;
    private LocalDate date;
    private Post post;
    private User user;
}
