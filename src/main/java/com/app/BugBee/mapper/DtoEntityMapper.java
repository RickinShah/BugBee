package com.app.BugBee.mapper;

import com.app.BugBee.dto.QueryDto;
import com.app.BugBee.dto.UserDto;
import com.app.BugBee.entity.Query;
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

    public static QueryDto queryToDto(Query query) {
        QueryDto queryDto = new QueryDto();
        BeanUtils.copyProperties(query, queryDto);
        return queryDto;
    }

    public static Query dtoToQuery(QueryDto queryDto) {
        Query query = new Query();
        BeanUtils.copyProperties(queryDto, query);
        return query;
    }

}
