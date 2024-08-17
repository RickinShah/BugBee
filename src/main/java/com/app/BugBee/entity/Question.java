package com.app.BugBee.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("queries")
public class Question {
    @Id
    private UUID id;
    @Column("user_id")
    private UUID userId;
    private String title;
    private String query;
    private String media;
    @Column("media_type")
    private String mediaType;
//    @Column("posted_date")
//    private
}
