package com.app.BugBee.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("bugbee.post_votes")
public class PostVote {
    @Id
    private long id;
    @Column("post_id")
    private long postId;
    @Column("user_id")
    private long userId;
    @Column("type_of_vote")
    private boolean typeOfVote;
}
