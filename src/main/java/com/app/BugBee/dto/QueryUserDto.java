package com.app.BugBee.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QueryUserDto {
    private QueryDto queryDto;
    private UserDto userDto;
}
