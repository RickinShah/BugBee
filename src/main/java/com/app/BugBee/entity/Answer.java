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
@Table("answers")
public class Answers {
    @Id
    private int id;
//    @Column("user_id")
//    private UUID userId;
    @Column("query_id")
    private UUID queryId;
    private int vote;
    private String answer;
    private User user;
}
