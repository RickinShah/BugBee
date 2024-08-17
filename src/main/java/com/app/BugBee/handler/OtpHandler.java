package com.app.BugBee.handler;

import com.app.BugBee.dto.AuthOtp;
import com.app.BugBee.dto.BooleanAndMessage;
import com.app.BugBee.entity.Otp;
import com.app.BugBee.entity.User;
import com.app.BugBee.repository.OtpRepository;
import com.app.BugBee.repository.UserRepository;
import com.app.BugBee.security.JwtTokenProvider;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
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
    private JavaMailSender mailSender;

    @Autowired
    private UserRepository repository;

    @Autowired
    private OtpRepository otpRepository;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Value("${spring_email}")
    private String from;

    public Mono<ServerResponse> sendOtp(ServerRequest request) {
        return request.bodyToMono(User.class)
                .flatMap(user -> repository.findByEmail(user.getEmail()))
                .flatMap(user -> {
                    int otpValue = new SecureRandom().nextInt(100000, 1000000);
                    return Mono.just(Otp.builder()
                                    .otp(otpValue)
                                    .userId(user.getId())
                                    .expirationTime(System.currentTimeMillis() + 15 * 60 * 1000)
                                    .build()
                            )
                            .flatMap(otpRepository::save)
                            .map(otpMono -> {
                                try {
                                    MimeMessage mimeMessage = mailSender.createMimeMessage();
                                    MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
                                    mimeMessageHelper.setFrom(from);
                                    mimeMessageHelper.setTo(user.getEmail());
                                    mimeMessageHelper.setSubject("One-Time Password from BugBee");
                                    mimeMessageHelper.setText(
                                            "Hello " + user.getName() + ", <strong>" + otpValue
                                                    + "</strong> is your One-Time Password(OTP) from BugBee. OTP is valid upto next 15 minutes.",
                                            true
                                    );
//                                    if(email.getAttachment() != null) {
//                                        FileSystemResource file = new FileSystemResource(new File(attachment));
//                                        mimeMessageHelper.addAttachment(Objects.requireNonNull(file.getFilename()), file);
//                                    }

                                    mailSender.send(mimeMessage);
                                    return new BooleanAndMessage(true, "Mail sent Successfully!");
                                } catch (Exception e) {
                                    log.info(e.toString());
                                    return new BooleanAndMessage(false, "Unable to send Otp!");
                                }

                            })
                            .defaultIfEmpty(new BooleanAndMessage(false, "Something went wrong!"));
                })
                .flatMap(result -> ServerResponse.ok().body(BodyInserters.fromValue(
                        new BooleanAndMessage(true, "OTP sent successfully!")))
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
}
