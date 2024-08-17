package com.app.BugBee.handler;

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
import org.springframework.http.HttpHeaders;
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
                            .doOnNext(System.out::println)
                            .flatMap(otpRepository::save)
                            .doOnNext(System.out::println)
                            .map(otpMono -> {
                                try {
                                    MimeMessage mimeMessage = mailSender.createMimeMessage();
                                    MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
                                    mimeMessageHelper.setFrom(from);
                                    mimeMessageHelper.setTo(user.getEmail());
                                    mimeMessageHelper.setSubject("One-Time Password from BugBee");
                                    mimeMessageHelper.setText(
                                            "Hello " + user.getName() + ", <strong>" + otpValue
                                                    + "</strong> is your One-Time Password(OTP) from BugBee",
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

//    public Mono<ServerResponse> validateOtp(ServerRequest request) {
//        return request.bodyToMono(Otp.class)
//                .flatMap(otpMono -> otpRepository.findById(tokenProvider.getUsername(token))
//                                .filterWhen(otpRepo -> Mono.just(
//                                otpRepo.getExpirationTime() >= System.currentTimeMillis()))
//                                .filterWhen(user -> Mono.just(user.getOtp() == otpMono.getOtp()))
////                        .filter()
////                        otpRepo.getOtp() == otpMono.getOtp() &&
//                )
//                .flatMap(e -> ServerResponse.ok().body(BodyInserters.fromValue(
//                        new BooleanAndMessage(true, "Valid Otp!")))
//                )
//                .switchIfEmpty(ServerResponse.badRequest().body(BodyInserters.fromValue(
//                        new BooleanAndMessage(false, "Invalid Otp!")
//                )));
//    }
}
