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
public class PostDto {
    private long postId;
    private String title;
    private String content;
    private String postType;
    private short upvoteCount;
    private short downvoteCount;
    private short commentCount;
    private boolean nsfwFlag;
    private LocalDate updatedAt;
    private boolean updateFlag;
    private UserInfoDto user;
    private boolean voteStatus;
}
