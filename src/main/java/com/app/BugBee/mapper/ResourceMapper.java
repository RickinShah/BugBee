package com.app.BugBee.mapper;

import com.app.BugBee.entity.Resource;
import io.r2dbc.spi.Row;

import java.util.function.BiFunction;

public class ResourceMapper implements BiFunction<Row, Object, Resource> {

    @Override
    public Resource apply(Row row, Object o) {
        if (row.get("file_format", Object.class) == null) {
            return Resource.builder()
                    .postId(row.get("post_pid", Long.class))
                    .fileFormat(row.get("file_format", String.class))
                    .build();

        }
        return Resource.builder()
                .postId(row.get("post_pid", Long.class))
                .nsfwFlag(row.get("nsfw_flag", Boolean.class))
                .fileFormat(row.get("file_format", String.class))
                .build();
    }
}
