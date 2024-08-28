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
    @Column("comment_pid")
    private long commentId;
    private String content;
    @Column("upvote_count")
    private int upvoteCount;
    @Column("upvote_count")
    private int downvoteCount;
    @Column("reply_count")
    private int replyCount;
    @Column("updated_at")
    private LocalDate updatedAt;
    @Column("update_flag")
    private boolean updateFlag;
    private Post post;
    private User user;
}
