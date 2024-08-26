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
@Table("bugbee.post_votes")
public class PostVote {
    @Id
    @Column("post_vote_id")
    private long postVoteId;
    @Column("post_id")
    private long postId;
    @Column("user_id")
    private long userId;
    @Column("upvote")
    private boolean upvote;
}
