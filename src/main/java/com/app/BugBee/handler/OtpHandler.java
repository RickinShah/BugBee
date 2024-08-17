package com.app.BugBee.handler;

import com.app.BugBee.dto.BooleanAndMessage;
import com.app.BugBee.repository.UserRepository;
import com.app.BugBee.security.JwtTokenProvider;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.io.File;
import java.security.SecureRandom;

@Service
public class MailHandler {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private UserRepository repository;

    @Value("${spring_email}")
    private String from;

    public Mono<ServerResponse> sendOtp(ServerRequest request) {
        return request.bodyToMono(String.class)
                .flatMap(repository::findByEmail)
                .map(user -> {
                    try {
                        MimeMessage mimeMessage = mailSender.createMimeMessage();
                        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
                        mimeMessageHelper.setFrom(from);
                        mimeMessageHelper.setTo(user.getEmail());
                        mimeMessageHelper.setSubject("One-Time Password from BugBee");
                        mimeMessageHelper.setText(
                                "Hello " + user.getName() + ", <strong>" +
                                        new SecureRandom().nextInt(100000, 1000000)
                                        + "</strong> is your One-Time Password(OTP) from BugBee", true
                        );

//                        if(email.getAttachment() != null) {
//                            FileSystemResource file = new FileSystemResource(new File(attachment));
//                            mimeMessageHelper.addAttachment(Objects.requireNonNull(file.getFilename()), file);
//                        }

                        mailSender.send(mimeMessage);
                        return true;
                    } catch (Exception e) {
                        return false;
                    }
                })
                .flatMap(result -> ServerResponse.ok().body(BodyInserters.fromValue(
                        new BooleanAndMessage(true, "OTP sent successfully!")))
                )
                .switchIfEmpty(ServerResponse.badRequest().body(BodyInserters.fromValue(
                        new BooleanAndMessage(false, "Invalid Email!")))
                );
    }
}
