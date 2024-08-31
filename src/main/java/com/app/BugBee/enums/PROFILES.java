package com.app.BugBee.enums;

import lombok.Getter;

@Getter
public enum PROFILES {
    P1(System.getenv("STORAGE") + "/profiles/p1.jpg"),
    P2(System.getenv("STORAGE") + "/profiles/p2.jpg"),
    P3(System.getenv("STORAGE") + "/profiles/p3.jpg"),
    P4(System.getenv("STORAGE") + "/profiles/p4.jpg");

    private final String value;
    PROFILES(String value) {this.value = value;}
}
