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
@Table("replies")
public class Reply {
    @Id
    private UUID id;
    @Column("user_id")
    private UUID userId;
    @Column("comment_id")
    private UUID commentId;
    private String reply;
    private short upvote;
    private short downvote;
    private Timestamp time;
}
