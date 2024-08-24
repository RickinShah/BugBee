package com.app.BugBee.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Component
public class UserDto {
    private long id;
    private String username;
    private String email;
    private String name;
    private boolean showNsfw;
}
