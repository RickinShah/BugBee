package com.app.BugBee.enums;

import lombok.Getter;

@Getter
public enum POST_TYPE {
//    QUESTION(null),
//    IMAGE(System.getenv("STORAGE") + "/posts/images"),
//    VIDEO(System.getenv("STORAGE") + "/posts/videos"),
//    AUDIO(System.getenv("STORAGE") + "/posts/audios"),
//    DOCUMENT(System.getenv("STORAGE") + "/posts/documents");

    QUESTION(new String[]{null, null}),
    IMAGE(new String[]{System.getenv("STORAGE") + "/posts/images", System.getenv("NGINX_STORAGE") + "/resource/images"}),
    VIDEO(new String[]{System.getenv("STORAGE") + "/posts/videos", System.getenv("NGINX_STORAGE") + "/resource/videos"}),
    AUDIO(new String[]{System.getenv("STORAGE") + "/posts/audios", System.getenv("NGINX_STORAGE") + "/resource/audios"}),
    DOCUMENT(new String[]{System.getenv("STORAGE") + "/posts/documents", System.getenv("NGINX_STORAGE") + "/resource/documents"});

    private final String[] values;

    POST_TYPE(String[] values) {
        this.values = values;
    }
}
