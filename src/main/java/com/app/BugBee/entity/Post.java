package com.app.BugBee.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table("bugbee.posts")
public class Post {
    @Id
    @Column("post_pid")
    private long postId;
    private String title;
    private String content;
    @Column("post_type")
    private String postType;
    @Column("upvote_count")
    private int upvoteCount;
    @Column("downvote_count")
    private int downvoteCount;
    @Column("comment_count")
    private int commentCount;
    @Column("updated_at")
    private LocalDate updatedAt;
    @Column("update_flag")
    private boolean updateFlag;
    private User user;
    private Resource resource;
}
