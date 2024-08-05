package com.app.BugBee.utils;

import com.app.BugBee.dto.UserDto;
import com.app.BugBee.dto.UserRegistrationDto;
import com.app.BugBee.entity.User;
import org.springframework.beans.BeanUtils;

public class AppUtils {
    public static UserDto UserToDto(User user) {
        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(user, userDto);
        return userDto;
    }

    public static User UserToEntity(UserDto userDto) {
        User user = new User();
        BeanUtils.copyProperties(userDto, user);
        return user;
    }

    public static UserRegistrationDto UserRegistrationToDto(User user) {
        UserRegistrationDto userRegistrationDto = new UserRegistrationDto();
        BeanUtils.copyProperties(user, userRegistrationDto);
        return userRegistrationDto;
    }

    public static User UserRegistrationToEntity(UserRegistrationDto userRegistrationDto) {
        User user = new User();
        BeanUtils.copyProperties(userRegistrationDto, user);
        return user;
    }

}
