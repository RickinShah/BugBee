package com.app.BugBee.dto;

import lombok.Data;

@Data
public class AuthOtp {
    private String email;
    private int otp;
}
