package com.app.BugBee.mapper;

import com.app.BugBee.dto.PostDto;
import com.app.BugBee.dto.ResourceDto;
import com.app.BugBee.dto.UserDto;
import com.app.BugBee.dto.UserInfoDto;
import com.app.BugBee.entity.Post;
import com.app.BugBee.entity.Resource;
import com.app.BugBee.entity.User;
import com.app.BugBee.enums.PROFILES;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

@Slf4j
public class DtoEntityMapper {
    public static UserDto userToDto(User user) {
        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(user, userDto);
        userDto.setProfilePath(PROFILES.valueOf(user.getProfile()).getValues()[1]);
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

        if (post.getUser() == null) {
            return postDto;
        }

        postDto.setUser(new UserInfoDto());
        BeanUtils.copyProperties(post.getUser(), postDto.getUser());
        postDto.getUser().setProfilePath(PROFILES.valueOf(post.getUser().getProfile()).getValues()[1]);

        if (post.getResource() == null) {
            return postDto;
        }

        postDto.setResource(new ResourceDto());
        BeanUtils.copyProperties(post.getResource(), postDto.getResource());

        return postDto;
    }

    public static Post dtoToPost(PostDto postDto) {
        Post post = new Post();
        BeanUtils.copyProperties(postDto, post, "user");

        if (postDto.getUser() == null) {
            return post;
        }
        post.setUser(new User());
        BeanUtils.copyProperties(postDto.getUser(), post.getUser());

        if (postDto.getResource() == null) {
            return post;
        }

        post.setResource(new Resource());
        BeanUtils.copyProperties(postDto.getResource(), post.getResource());

        return post;
    }

}
