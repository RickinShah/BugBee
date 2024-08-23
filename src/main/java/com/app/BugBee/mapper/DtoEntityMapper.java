package com.app.BugBee.mapper;

import com.app.BugBee.dto.PostDto;
import com.app.BugBee.dto.UserDto;
import com.app.BugBee.entity.Post;
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

    public static PostDto postToDto(Post post) {
        PostDto postDto = new PostDto();
        BeanUtils.copyProperties(post, postDto);
        return postDto;
    }

    public static Post dtoToPost(PostDto postDto) {
        Post post = new Post();
        BeanUtils.copyProperties(postDto, post);
        return post;
    }

}
