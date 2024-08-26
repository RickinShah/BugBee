package com.app.BugBee.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostDto {
    private long postId;
    private String title;
    private String content;
    private String typeOfPost;
    private short upvote;
    private short downvote;
    private short totalComments;
    private boolean isNsfw;
    private LocalDate date;
    private UserDto user;
}
