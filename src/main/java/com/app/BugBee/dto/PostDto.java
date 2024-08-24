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
@Builder
public class PostDto {
    private long id;
    private long userId;
    private String username;
    private String title;
    private String post;
    private String typeOfPost;
    private short upvote;
    private short downvote;
    private boolean isNsfw;
    private LocalDate date;
//    private Timestamp
}
