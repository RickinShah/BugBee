package com.app.BugBee.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("bugbee.users")
@Builder
public class User {
    @Id
    @Column("user_id")
    private long userId;
    private String username;
    private String email;
    private String name;
    private String password;
    private String roles;
    @Column("show_nsfw")
    private boolean showNsfw = false;

    public User(String email, String password, String roles) {
        this.email = email;
        this.password = password;
        this.roles = roles;
    }
}
