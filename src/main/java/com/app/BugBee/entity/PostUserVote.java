package com.app.BugBee.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table("bugbee.post_user_vote")
public class PostUserVote {
    @Id
    @Column("vote_pid")
    private long voteId;
    private long postId;
    private long userId;
    //    private Post post;
//    private User user;
    @Column("vote_status")
    private boolean voteStatus;
}
