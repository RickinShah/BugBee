package com.app.BugBee.repository.custom.impl;

import com.app.BugBee.entity.User;
import com.app.BugBee.mapper.UserMapper;
import com.app.BugBee.repository.custom.CustomUserRepository;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class CustomUserRepositoryImpl implements CustomUserRepository {

    private final DatabaseClient client;
    private final UserMapper userMapper;

    public CustomUserRepositoryImpl(DatabaseClient client, UserMapper userMapper) {
        this.client = client;
        this.userMapper = userMapper;
    }

    @Override
    public Mono<User> findByUsername(String username) {
        final String query = "SELECT * FROM bugbee.users u LEFT OUTER JOIN bugbee.profiles p" +
                " ON u.profile_id = p.profile_pid WHERE u.username = :username";

        return client.sql(query)
                .bind("username", username)
                .map(userMapper::apply)
                .first();
    }

    @Override
    public Mono<User> findByUsernameOrEmail(String username, String email) {
        final String query = "SELECT * FROM bugbee.users u LEFT OUTER JOIN bugbee.profiles p" +
                " ON u.profile_id = p.profile_pid WHERE u.username = :username OR u.email = :email";

        return client.sql(query)
                .bind("username", username)
                .bind("email", email)
                .map(userMapper::apply)
                .first();
    }

    @Override
    public Mono<User> findByUserId(long userId) {
        final String query = "SELECT * FROM bugbee.users u LEFT OUTER JOIN bugbee.profiles p" +
                " ON u.profile_id = p.profile_pid WHERE u.user_pid = :userId";

        return client.sql(query)
                .bind("userId", userId)
                .map(userMapper::apply)
                .first();
    }

    @Override
    public Mono<User> saveUser(User user) {
        if (user.getUserId() != 0) {
            final String query = "WITH updated AS (UPDATE bugbee.users SET username = :username, email = :email, name = :name," +
                    " password = :password, roles = :roles, show_nsfw = :showNsfw, profile_id = :profileId" +
                    " WHERE user_pid = :userId RETURNING *)" +
                    "SELECT * FROM updated u LEFT OUTER JOIN bugbee.profiles p" +
                    " ON u.profile_id = p.profile_pid";

            return client.sql(query)
                    .bind("username", user.getUsername())
                    .bind("email", user.getEmail())
                    .bind("name", user.getName())
                    .bind("password", user.getPassword())
                    .bind("roles", user.getRoles())
                    .bind("showNsfw", user.isShowNsfw())
                    .bind("profileId", user.getProfile().getProfileId())
                    .map(userMapper::apply)
                    .first();
        }
        final String query = "WITH inserted AS (INSERT INTO bugbee.users(username, email, name, password, roles, show_nsfw, profile_id)" +
                " VALUES (:username, :email, :name, :password, :roles, :showNsfw, :profileId) RETURNING *)" +
                "SELECT * FROM inserted u LEFT OUTER JOIN bugbee.profiles p" +
                " ON u.profile_id = p.profile_pid";

        return client.sql(query)
                .bind("username", user.getUsername())
                .bind("email", user.getEmail())
                .bind("name", user.getName())
                .bind("password", user.getPassword())
                .bind("roles", user.getRoles())
                .bind("showNsfw", user.isShowNsfw())
                .bind("profileId", user.getProfile().getProfileId())
                .map(userMapper::apply)
                .first();
    }

    public Flux<User> findAll() {
        final String query = "SELECT * FROM bugbee.users u LEFT OUTER JOIN bugbee.profiles p" +
                " ON u.profile_id = p.profile_pid";

        return client.sql(query)
                .map(userMapper::apply)
                .all();
    }
}
