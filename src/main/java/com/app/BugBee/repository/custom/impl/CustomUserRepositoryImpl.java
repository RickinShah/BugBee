package com.app.BugBee.repository.custom.impl;

import com.app.BugBee.entity.User;
import com.app.BugBee.mapper.UserMapper;
import com.app.BugBee.repository.custom.CustomUserRepository;
import io.r2dbc.spi.Parameter;
import io.r2dbc.spi.Parameters;
import io.r2dbc.spi.R2dbcType;
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
        final String query = "SELECT * FROM bugbee.users u" +
                " WHERE u.username = :username";

        return client.sql(query)
                .bind("username", username)
                .map(userMapper::apply)
                .first();
    }

    @Override
    public Mono<User> findByUsernameOrEmail(String username, String email) {
        final String query = "SELECT * FROM bugbee.users u" +
                " WHERE u.username = :username OR u.email = :email";

        return client.sql(query)
                .bind("username", username)
                .bind("email", email)
                .map(userMapper::apply)
                .first();
    }

    @Override
    public Mono<User> findByUserId(long userId) {
        final String query = "SELECT * FROM bugbee.users u" +
                " WHERE u.user_pid = :userId";

        return client.sql(query)
                .bind("userId", userId)
                .map(userMapper::apply)
                .first();
    }

    @Override
    public Mono<User> saveUser(User user) {
        if (user.getUserId() != 0) {
            final String query = "UPDATE bugbee.users SET username = :username, email = :email, name = :name," +
                    " password = :password, roles = :roles, show_nsfw = :showNsfw, profile = :profile, bio = :bio" +
                    " WHERE user_pid = :userId RETURNING *";

            return client.sql(query)
                    .bind("username", user.getUsername())
                    .bind("email", user.getEmail())
                    .bind("name", user.getName())
                    .bind("password", user.getPassword())
                    .bind("roles", user.getRoles())
                    .bind("showNsfw", user.isShowNsfw())
                    .bind("profile", user.getProfile())
                    .bind("bio", Parameters.in(R2dbcType.VARCHAR, user.getBio()))
                    .bind("userId", user.getUserId())
                    .map(userMapper::apply)
                    .first();
        }
        final String query = "INSERT INTO bugbee.users(username, email, name, password, roles, show_nsfw, profile, bio)" +
                " VALUES (:username, :email, :name, :password, :roles, :showNsfw, :profile, :bio) RETURNING *";

        return client.sql(query)
                .bind("username", user.getUsername())
                .bind("email", user.getEmail())
                .bind("name", user.getName())
                .bind("password", user.getPassword())
                .bind("roles", user.getRoles())
                .bind("showNsfw", user.isShowNsfw())
                .bind("profile", user.getProfile())
                .bind("bio", Parameters.in(R2dbcType.VARCHAR, user.getBio()))
                .map(userMapper::apply)
                .first();
    }

    public Flux<User> findAll() {
        final String query = "SELECT * FROM bugbee.users";

        return client.sql(query)
                .map(userMapper::apply)
                .all();
    }
}
