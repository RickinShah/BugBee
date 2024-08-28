package com.app.BugBee.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Profile {
    @Id
    @Column("profile_pid")
    private int profileId;
    @Column("profile_file_path")
    private String profileFilePath;
}
