package com.rawafed.employeemgmt.service.impl;

import com.rawafed.employeemgmt.domain.Mail;
import com.rawafed.employeemgmt.service.IMailService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailService implements IMailService {
    private final JavaMailSender javaMailSender;

    @Override
    @CircuitBreaker(name = "mailgun-service", fallbackMethod = "sendEmailFallback")
    public void sendMail(Mail mail) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper;

        try {
            helper = new MimeMessageHelper(mimeMessage, true);
            helper.setFrom(mail.getFrom());
            helper.setTo(mail.getTo());
            helper.setSubject(mail.getSubject());
            helper.setText(mail.getBody());

            javaMailSender.send(mimeMessage);
        } catch (MessagingException | MailException e) {
            log.error("Something went wrong while sending the mail, error: {}", e.getMessage());
        }
    }

    public void sendEmailFallback(Mail mail, Throwable throwable) {
        // Some logic to handel fallback scenarios
        log.info("Something went wrong while trying to send this mail: {}", mail);
        log.info("Fallback triggered: " + throwable.getMessage());
    }

}


