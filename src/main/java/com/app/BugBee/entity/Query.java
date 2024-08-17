package com.app.BugBee.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("queries")
public class Query {
    @Id
    private UUID id;
    private String title;
    private String query;
    private String media;
    @Column("media_type")
    private String mediaType;
    private UUID userId;
    private Flux<Answer> answers;
}
