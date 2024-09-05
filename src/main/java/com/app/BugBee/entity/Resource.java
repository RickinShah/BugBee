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
@Table("resources")
@Builder
public class Resource {
    @Id
    @Column("post_pid")
    private long postId;
    @Column("nsfw_flag")
    private boolean nsfwFlag;
    @Column("file_format")
    private String fileFormat;
    @Column("secret_key")
    private String secretKey;
    @Column("iv")
    private byte[] iv;
}
