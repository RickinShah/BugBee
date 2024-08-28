package com.app.BugBee.dto;

import com.app.BugBee.entity.Profile;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private long userId;
    private String username;
    private String email;
    private String name;
    private boolean showNsfw;
    private Profile profile;
}
