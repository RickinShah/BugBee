package com.app.BugBee.mapper;

import com.app.BugBee.dto.QueryDto;
import com.app.BugBee.dto.UserDto;
import com.app.BugBee.entity.Question;
import com.app.BugBee.entity.User;
import org.springframework.beans.BeanUtils;

public class DtoEntityMapper {
    public static UserDto userToDto(User user) {
        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(user, userDto);
        return userDto;
    }

    public static User dtoToUser(UserDto userDto) {
        User user = new User();
        BeanUtils.copyProperties(userDto, user);
        return user;
    }

    public static QueryDto queryToDto(Question question) {
        QueryDto queryDto = new QueryDto();
        BeanUtils.copyProperties(question, queryDto);
        return queryDto;
    }

    public static Question dtoToQuery(QueryDto queryDto) {
        Question question = new Question();
        BeanUtils.copyProperties(queryDto, question);
        return question;
    }

}
