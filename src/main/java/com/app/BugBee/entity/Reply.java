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
    @Column("reply_pid")
    private long replyId;
    private String content;
    @Column("upvote_count")
    private int upvoteCount;
    @Column("downvote_count")
    private int downvoteCount;
    @Column("updated_at")
    private LocalDate updatedAt;
    @Column("update_flag")
    private boolean updateFlag;
    private User user;
    private Comment comment;
}
