package com.app.BugBee.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QueryDto {
//    private UUID id;
    private String title;
    private String query;
    private String media;
    private String mediaType;
}
