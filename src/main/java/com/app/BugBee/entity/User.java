package com.app.BugBee.entity;

import com.app.BugBee.enums.PROFILES;
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
    @Column("user_pid")
    private long userId;
    private String username;
    private String email;
    private String name;
    private String password;
    private String roles;
    private String bio;
    @Column("show_nsfw")
    private boolean showNsfw = false;
    private String profile = PROFILES.P1.name();

    public User(String email, String password, String roles) {
        this.email = email;
        this.password = password;
        this.roles = roles;
    }
}
