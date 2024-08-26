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
@Table("bugbee.comment_votes")
public class CommentVote {
    @Id
    @Column("comment_vote_id")
    private long commentVoteId;
    @Column("comment_id")
    private long commentId;
    @Column("user_id")
    private long userId;
    @Column("upvote")
    private boolean upvote;
}
