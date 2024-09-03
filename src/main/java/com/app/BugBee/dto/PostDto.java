package com.app.BugBee.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostDto {
    private long postId;
    private String title;
    private String content;
    private String postType;
    private int upvoteCount;
    private int downvoteCount;
    private int commentCount;
    private LocalDate updatedAt;
    private boolean updateFlag;
    private UserInfoDto user;
    private boolean voteStatus;
    private boolean votedFlag;
    private ResourceDto resource;

    public PostDto(Map<String, Object> postMap, long userId) {
        this.postId = Long.parseLong(postMap.getOrDefault("postId", "0").toString());
        this.title = (String) postMap.getOrDefault("title", null);
        this.content = (String) postMap.getOrDefault("content", null);
        this.postType = (String) postMap.getOrDefault("postType", null);
        this.upvoteCount = Integer.parseInt(postMap.getOrDefault("upvoteCount", "0").toString());
        this.downvoteCount = Integer.parseInt(postMap.getOrDefault("downvoteCount", "0").toString());
        this.commentCount = Integer.parseInt(postMap.getOrDefault("commentCount", "0").toString());
        this.updatedAt = LocalDate.now();
        this.updateFlag = Boolean.parseBoolean(postMap.getOrDefault("updateFlag", "false").toString());
        this.voteStatus = Boolean.parseBoolean(postMap.getOrDefault("voteStatus", "false").toString());
        this.votedFlag = Boolean.parseBoolean(postMap.getOrDefault("votedFlag", "false").toString());
        this.user = new UserInfoDto(userId, null, null);
    }
}
