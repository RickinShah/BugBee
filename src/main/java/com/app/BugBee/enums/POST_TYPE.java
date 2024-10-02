package com.app.BugBee.enums;

import lombok.Getter;

import java.io.File;

@Getter
public enum POST_TYPE {
    QUESTION(new String[]{null, null}),
    IMAGE(new String[]{System.getenv("STORAGE") + File.separator + "posts" + File.separator + "images", System.getenv("POST_URL")}),
    VIDEO(new String[]{System.getenv("STORAGE") + File.separator + "posts" + File.separator + "videos", System.getenv("POST_URL")}),
    AUDIO(new String[]{System.getenv("STORAGE") + File.separator + "posts" + File.separator + "audios", System.getenv("POST_URL")}),
    DOCUMENT(new String[]{System.getenv("STORAGE") + File.separator + "posts" + File.separator + "documents", System.getenv("POST_URL")});

    private final String[] values;

    POST_TYPE(String[] values) {
        this.values = values;
    }
}
