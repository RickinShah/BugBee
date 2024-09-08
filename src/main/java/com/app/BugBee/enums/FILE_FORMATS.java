package com.app.BugBee.enums;

import lombok.Getter;

@Getter
public enum FILE_FORMATS {
    PNG(".png"),
    JPG(".jpg"),
    JPEG(".jpeg"),
    MP4(".mp4"),
    WEBM(".webm"),
    MP3(".mp3"),
    WAV(".wav"),
    OGG(".ogg"),
    PDF(".pdf");

    public final String value;

    FILE_FORMATS(String value) {
        this.value = value;
    }
}
