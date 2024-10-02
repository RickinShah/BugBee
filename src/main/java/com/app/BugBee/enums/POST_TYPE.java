package com.app.BugBee.enums;

import lombok.Getter;

@Getter
public enum POST_TYPE {
    QUESTION(new String[]{null, null}),
    IMAGE(new String[]{System.getenv("STORAGE") + "/posts/images", System.getenv("POST_URL")}),
    VIDEO(new String[]{System.getenv("STORAGE") + "/posts/videos", System.getenv("POST_URL")}),
    AUDIO(new String[]{System.getenv("STORAGE") + "/posts/audios", System.getenv("POST_URL")}),
    DOCUMENT(new String[]{System.getenv("STORAGE") + "/posts/documents", System.getenv("POST_URL")});

    private final String[] values;

    POST_TYPE(String[] values) {
        this.values = values;
    }
}
