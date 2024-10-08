package com.app.BugBee.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {
    private long userId;
    private String username;
    private String email;
    private String name;
    private boolean showNsfw;
    private String profile;
    private String profilePath;
    private String bio;
}
