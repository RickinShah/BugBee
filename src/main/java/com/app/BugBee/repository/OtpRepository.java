package com.app.BugBee.repository;

import com.app.BugBee.entity.Otp;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@SuppressWarnings("unchecked")
@Repository
public interface OtpRepository extends R2dbcRepository<Otp, Long> {

    @SuppressWarnings("NullableProblems")
    @Modifying
    @Query("INSERT INTO bugbee.otps(user_pid, otp, expiration_time) VALUES (:#{#otp.userId}, :#{#otp.otp}, :#{#otp.expirationTime}) ON CONFLICT (user_pid) DO UPDATE SET otp=:#{#otp.otp}, expiration_time=:#{#otp.expirationTime}")
    Mono<Long> save(final Otp otp);
}
