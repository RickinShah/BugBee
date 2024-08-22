package com.app.BugBee.handler;

import com.app.BugBee.dto.AuthOtp;
import com.app.BugBee.dto.BooleanAndMessage;
import com.app.BugBee.entity.Otp;
import com.app.BugBee.entity.User;
import com.app.BugBee.repository.OtpRepository;
import com.app.BugBee.repository.UserRepository;
import com.app.BugBee.security.JwtTokenProvider;
import com.app.BugBee.utils.MailSenderUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.security.SecureRandom;

@Slf4j
@Service
public class OtpHandler {

    @Autowired
    private UserRepository repository;

    @Autowired
    private OtpRepository otpRepository;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    MailSenderUtils mailSender;

    public Mono<ServerResponse> sendOtp(ServerRequest request) {
        return request.bodyToMono(User.class)
                .flatMap(user -> repository.findByEmail(user.getEmail()))
                .flatMap(this::createOtpAndSendOtpMail)
                .flatMap(result -> ServerResponse.ok().body(BodyInserters.fromValue(
                        new BooleanAndMessage(result.isSuccess(), result.getMessage())))
                )
                .switchIfEmpty(ServerResponse.badRequest().body(BodyInserters.fromValue(
                        new BooleanAndMessage(false, "Invalid Email!")))
                );
    }

    public Mono<ServerResponse> validateOtp(ServerRequest request) {
        Mono<AuthOtp> authOtpMono = request.bodyToMono(AuthOtp.class);
        return authOtpMono
                .flatMap(authOtp -> repository.findByEmail(authOtp.getEmail())
                        .flatMap(user -> otpRepository.findById(user.getId()))
                        .switchIfEmpty(Mono.error(new RuntimeException("OTP not found!")))
                        .filter(otp -> otp.getExpirationTime() >= System.currentTimeMillis())
                        .switchIfEmpty(Mono.error(new RuntimeException("OTP Expired!")))
                        .filter(otp -> otp.getOtp() == authOtp.getOtp())
                        .flatMap(otp -> ServerResponse.ok().body(BodyInserters.fromValue(
                                new BooleanAndMessage(true, "Valid OTP!")))
                        )
                        .onErrorResume(RuntimeException.class, e -> ServerResponse.badRequest().body(BodyInserters.fromValue(
                                new BooleanAndMessage(false, e.getMessage())
                        )))
                        .switchIfEmpty(ServerResponse.badRequest().body(BodyInserters.fromValue(
                                new BooleanAndMessage(false, "Incorrect OTP!")
                        )))
                );

    }

    public Mono<BooleanAndMessage> createOtpAndSendOtpMail(User user) {
        int otpValue = new SecureRandom().nextInt(100000, 1000000);
        Otp otp = Otp.builder()
                .otp(otpValue)
                .userId(user.getId())
                .expirationTime(System.currentTimeMillis() + 15 * 60 * 1000)
                .build();

        return otpRepository.save(otp)
                .flatMap(otpObj -> repository.findById(otp.getUserId()))
                .map(e -> mailSender.sendMail(user.getEmail(),
                        "One-Time Password from BugBee",
                        "Hello " + user.getName() + ", <strong>" + otpValue
                                + "</strong> is your One-Time Password(OTP) from BugBee. OTP is valid upto next 15 minutes.",
                        null
                        ))
                .defaultIfEmpty(new BooleanAndMessage(false, "Something went wrong!"));
    }

}
