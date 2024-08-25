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
@Table("bugbee.reply_votes")
public class ReplyVote {
    @Id
    private long id;
    @Column("reply_id")
    private long replyId;
    @Column("user_id")
    private long userId;
    @Column("type_of_vote")
    private boolean typeOfVote;
}
