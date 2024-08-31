package com.app.BugBee.enums;

import lombok.Getter;

@Getter
public enum POST_TYPE {
    QUESTION(null),
    IMAGE(System.getenv("STORAGE") + "/posts/images"),
    VIDEO(System.getenv("STORAGE") + "/posts/videos"),
    AUDIO(System.getenv("STORAGE") + "/posts/audios"),
    DOCUMENT(System.getenv("STORAGE") + "/posts/documents");

    private final String value;
    POST_TYPE(String value) {this.value = value;}
}
