package com.app.BugBee.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostUserVoteDto {
    private PostDto post;
    private PostUserInfoDto user;
    private boolean voteStatus;
}
