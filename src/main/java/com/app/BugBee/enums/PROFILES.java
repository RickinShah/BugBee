package com.app.BugBee.enums;

import lombok.Getter;

@Getter
public enum PROFILES {
    P1(new String[]{System.getenv("STORAGE") + "/profiles/p1.jpg", System.getenv("NGINX_STORAGE" + "/profiles/p1.jpg")}),
    P2(new String[]{System.getenv("STORAGE") + "/profiles/p2.jpg", System.getenv("NGINX_STORAGE" + "/profiles/p2.jpg")}),
    P3(new String[]{System.getenv("STORAGE") + "/profiles/p3.jpg", System.getenv("NGINX_STORAGE" + "/profiles/p3.jpg")}),
    P4(new String[]{System.getenv("STORAGE") + "/profiles/p4.jpg", System.getenv("NGINX_STORAGE" + "/profiles/p4.jpg")});

    private final String[] values;

    PROFILES(String[] values) {
        this.values = values;
    }
}
