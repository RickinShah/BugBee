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
@Table("bugbee.replies")
public class Reply {
    @Id
    @Column("reply_id")
    private long replyId;
    private String reply;
    private short upvote;
    private short downvote;
    private LocalDate date;
    private User user;
    private Comment comment;
}
