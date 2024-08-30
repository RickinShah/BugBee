package com.app.BugBee.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("bugbee.reply_user_vote")
public class ReplyUserVote {
    @Id
    @Column("vote_pid")
    private long voteId;
    @Column("user_id")
    private long userId;
    @Column("reply_id")
    private long replyId;
    @Column("vote_status")
    private boolean voteStatus;
}
