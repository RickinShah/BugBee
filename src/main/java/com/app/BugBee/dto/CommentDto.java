package com.app.BugBee.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentDto {
    private long commentId;
    private String content;
    private int upvoteCount;
    private int downvoteCount;
    private int replyCount;
    private LocalDate updatedAt;
    private boolean updateFlag;
    private PostDto post;
    private UserInfoDto user;
}
