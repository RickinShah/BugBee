package com.app.BugBee.mapper;

import com.app.BugBee.entity.Profile;
import com.app.BugBee.entity.User;
import io.r2dbc.spi.Row;
import org.springframework.stereotype.Component;

import java.util.function.BiFunction;

@Component
public class UserMapper implements BiFunction<Row, Object, User> {

    @Override
    public User apply(Row row, Object o) {
        return User.builder()
                .userId(row.get("user_pid", Long.class))
                .username(row.get("username", String.class))
                .email(row.get("email", String.class))
                .name(row.get("name", String.class))
                .password(row.get("password", String.class))
                .roles(row.get("roles", String.class))
                .showNsfw(row.get("show_nsfw", Boolean.class))
                .profile(new Profile(
                        row.get("profile_pid", Integer.class),
                        row.get("profile_file_path", String.class)
                ))
                .build();
    }
}
