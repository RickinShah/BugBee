package com.app.BugBee.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "otps")
public class Otp {
    @Id
    @Column("user_id")
    private UUID userId;
    private int otp;
    @Column("expiration_time")
    long expirationTime;
}
