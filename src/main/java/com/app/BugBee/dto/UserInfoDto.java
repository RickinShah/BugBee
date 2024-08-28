package com.app.BugBee.dto;

import com.app.BugBee.entity.Profile;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserInfoDto {
    private long userId;
    private String username;
    private Profile profile;
}
