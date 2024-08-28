package com.app.BugBee.mapper;

import com.app.BugBee.dto.PostDto;
import com.app.BugBee.dto.UserDto;
import com.app.BugBee.dto.UserInfoDto;
import com.app.BugBee.entity.Post;
import com.app.BugBee.entity.Profile;
import com.app.BugBee.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

@Slf4j
public class DtoEntityMapper {
    public static UserDto userToDto(User user) {
        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(user, userDto);
        if (user.getProfile() != null) {
            userDto.setProfile(new Profile());
            BeanUtils.copyProperties(user.getProfile(), userDto.getProfile());
        }
        return userDto;
    }

    public static User dtoToUser(UserDto userDto) {
        User user = new User();
        BeanUtils.copyProperties(userDto, user);
        if (userDto.getProfile() != null) {
            user.setProfile(new Profile());
            ;
            BeanUtils.copyProperties(userDto.getProfile(), user);
        }
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

        if (post.getUser().getProfile() == null) {
            return postDto;
        }

        postDto.getUser().setProfile(new Profile());
        BeanUtils.copyProperties(post.getUser().getProfile(), postDto.getUser().getProfile());

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

        if (postDto.getUser().getProfile() == null) {
            return post;
        }

        post.getUser().setProfile(new Profile());
        BeanUtils.copyProperties(postDto.getUser().getProfile(), post.getUser().getProfile());

        return post;
    }

}
