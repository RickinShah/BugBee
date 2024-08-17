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
public class Answer {
    @Id
    private int id;
    @Column("query_id")
    private UUID queryId;
    @Column("user_id")
    private UUID userId;
    private int vote;
    private String answer;
}
