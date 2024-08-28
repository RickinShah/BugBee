package com.app.BugBee.utils;

import com.app.BugBee.dto.BooleanAndMessage;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Objects;

@Slf4j
@Component
public class MailSenderUtils {

    private final JavaMailSender mailSender;

    @Value("${MAIL_EMAIL}")
    private String from;

    public MailSenderUtils(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public BooleanAndMessage sendMail(String to, String subject, String body, String attachment) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
            mimeMessageHelper.setFrom(from);
            mimeMessageHelper.setTo(to);
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setText(body, true);
            if (attachment != null) {
                FileSystemResource file = new FileSystemResource(new File(attachment));
                mimeMessageHelper.addAttachment(Objects.requireNonNull(file.getFilename()), file);
            }

            mailSender.send(mimeMessage);
            return new BooleanAndMessage(true, "Mail sent Successfully!");

        } catch (Exception e) {
            log.info("Unable to send mail to {} due to {}", to, e.getMessage());
            return new BooleanAndMessage(false, "Unable to send Mail!");
        }

    }
}
