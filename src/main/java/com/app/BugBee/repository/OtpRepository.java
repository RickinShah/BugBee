package com.app.BugBee.repository;

import com.app.BugBee.entity.Otp;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface OtpRepository extends R2dbcRepository<Otp, UUID> {

}
