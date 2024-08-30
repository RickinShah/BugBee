package com.app.BugBee.enums;

import lombok.Getter;

@Getter
public enum POST_TYPE {
    QUESTION(null),
    IMAGE("path/to/images"),
    VIDEO("path/to/videos"),
    DOCUMENT("path/to/documents");

    private final String value;
    POST_TYPE(String value) {this.value = value;}
}
