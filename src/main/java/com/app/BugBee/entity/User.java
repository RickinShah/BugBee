package com.app.BugBee.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
@Builder
public class User {
    enum ACCOUNT {
        ADMIN,
        FACULTY,
        USER
    }

    @Id
    private UUID id;
    private String email;
    private String name;
    private String password;
    private String account = ACCOUNT.USER.name();
}
