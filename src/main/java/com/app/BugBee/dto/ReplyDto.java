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
public class ReplyDto {
    private long replyId;
    private String content;
    private int upvoteCount;
    private int downvoteCount;
    private LocalDate updatedAt;
    private boolean updateFlag;
    private CommentDto comment;
    private UserInfoDto user;
    private boolean voteStatus;
    private boolean votedFlag;
}
