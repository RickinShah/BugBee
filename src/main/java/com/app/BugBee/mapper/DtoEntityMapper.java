package com.app.BugBee.mapper;

import com.app.BugBee.dto.*;
import com.app.BugBee.entity.*;
import com.app.BugBee.enums.FILE_FORMATS;
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

    public static UserInfoDto userInfoToDto(User user) {
        UserInfoDto userInfoDto = new UserInfoDto();
        BeanUtils.copyProperties(user, userInfoDto);
        userInfoDto.setProfilePath(PROFILES.valueOf(user.getProfile()).getValues()[1]);
        return userInfoDto;
    }

    public static PostDto postToDto(Post post) {
        PostDto postDto = new PostDto();
        BeanUtils.copyProperties(post, postDto);

        if (post.getUser() != null) {
            postDto.setUser(new UserInfoDto());
            BeanUtils.copyProperties(post.getUser(), postDto.getUser());
            postDto.getUser().setProfilePath(PROFILES.valueOf(post.getUser().getProfile()).getValues()[1]);
        }
        if (post.getResource() != null) {
            postDto.setResource(new ResourceDto());
            BeanUtils.copyProperties(post.getResource(), postDto.getResource());
            postDto.getResource().setFileFormat(FILE_FORMATS.valueOf(postDto.getResource().getFileFormat()).getValue());
        }
        return postDto;
    }

    public static Post dtoToPost(PostDto postDto) {
        Post post = new Post();
        BeanUtils.copyProperties(postDto, post, "user");

        if (postDto.getUser() != null) {
            post.setUser(new User());
            BeanUtils.copyProperties(postDto.getUser(), post.getUser());
        }
        if (postDto.getResource() != null) {
            post.setResource(new Resource());
            BeanUtils.copyProperties(postDto.getResource(), post.getResource());
        }

        return post;
    }

    public static Comment dtoToComment(CommentDto commentDto) {
        Comment comment = new Comment();
        BeanUtils.copyProperties(commentDto, comment);

        if (commentDto.getUser() != null) {
            comment.setUser(new User());
            BeanUtils.copyProperties(commentDto.getUser(), comment.getUser());
        }

        if (commentDto.getPost() != null) {
            comment.setPost(new Post());
            BeanUtils.copyProperties(commentDto.getPost(), comment.getPost());
            if (commentDto.getPost().getResource() != null) {
                comment.getPost().setResource(new Resource());
                BeanUtils.copyProperties(commentDto.getPost().getResource(), comment.getPost().getResource());
            }
        }


        return comment;
    }

    public static CommentDto commentToDto(Comment comment) {
        CommentDto commentDto = new CommentDto();
        BeanUtils.copyProperties(comment, commentDto);

        if (comment.getUser() != null) {
            commentDto.setUser(new UserInfoDto());
            BeanUtils.copyProperties(comment.getUser(), commentDto.getUser());
        }
        if (comment.getPost() != null) {
            commentDto.setPost(new PostDto());
            BeanUtils.copyProperties(comment.getPost(), commentDto.getPost());
            if (comment.getPost().getResource() != null) {
                commentDto.getPost().setResource(new ResourceDto());
                BeanUtils.copyProperties(comment.getPost().getResource(), commentDto.getPost().getResource());
            }
        }

        return commentDto;
    }

    public static ReplyDto replyToDto(Reply reply) {
        ReplyDto replyDto = new ReplyDto();
        BeanUtils.copyProperties(reply, replyDto);

        if (reply.getUser() != null) {
            replyDto.setUser(new UserInfoDto());
            BeanUtils.copyProperties(reply.getUser(), replyDto.getUser());
        }

        if (reply.getComment() != null) {
            replyDto.setComment(new CommentDto());
            BeanUtils.copyProperties(reply.getComment(), replyDto.getComment());
        }

        return replyDto;
    }

    public static Reply dtoToReply(ReplyDto replyDto) {
        Reply reply = new Reply();
        BeanUtils.copyProperties(replyDto, reply);

        if (replyDto.getUser() != null) {
            reply.setUser(new User());
            BeanUtils.copyProperties(replyDto.getUser(), reply.getUser());
        }

        if (replyDto.getComment() != null) {
            reply.setComment(new Comment());
            BeanUtils.copyProperties(replyDto.getComment(), reply.getComment());
        }

        return reply;
    }

}
