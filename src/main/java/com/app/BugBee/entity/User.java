package com.app.BugBee.entity;

import com.app.BugBee.enums.ACCOUNT;
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
    @Id
    private UUID id;
    private String email;
    private String name;
    private String password;
    private String account = ACCOUNT.USER.name();

    public User(String email, String password, String account) {
        this.email = email;
        this.password = password;
        this.account = account;
    }
}
