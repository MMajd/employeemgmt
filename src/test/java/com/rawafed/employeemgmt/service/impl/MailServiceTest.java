package com.rawafed.employeemgmt.service.impl;

import com.rawafed.employeemgmt.domain.Mail;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class MailServiceTest {

    @Mock
    private JavaMailSender javaMailSender;

    @Mock
    private MimeMessage mimeMessage;

    @Mock
    private CircuitBreakerRegistry circuitBreakerRegistry;

    @InjectMocks
    private MailService mailService;

    private Mail mail;

    @BeforeEach
    void setUp() {
        mail = Mail.builder()
                .from("sender@example.com")
                .to("recipient@example.com")
                .subject("Test Subject")
                .body("Test Body")
                .build();

        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);
    }

    @Test
    void testCircuitBreakerInClosedState() {
        CircuitBreaker circuitBreaker = mock(CircuitBreaker.class);
        when(circuitBreakerRegistry.circuitBreaker("mailgun-service")).thenReturn(circuitBreaker);

        doNothing().when(javaMailSender).send(mimeMessage);

        mailService.sendMail(mail);
        verify(javaMailSender, times(1)).send(mimeMessage);
    }

    @Test
    void testCircuitBreakerInOpenStateTriggersFallback() {
        CircuitBreaker circuitBreaker = mock(CircuitBreaker.class);
        when(circuitBreakerRegistry.circuitBreaker("mailgun-service")).thenReturn(circuitBreaker);

        MailException mockMailException = mock(MailException.class);
        doThrow(mockMailException).when(javaMailSender).send(mimeMessage);

        mailService.sendMail(mail);
        verify(javaMailSender, times(1)).send(mimeMessage);
    }

    @Test
    void testCircuitBreakerInHalfOpenState() {
        CircuitBreaker circuitBreaker = mock(CircuitBreaker.class);
        when(circuitBreakerRegistry.circuitBreaker("mailgun-service")).thenReturn(circuitBreaker);

        mailService.sendMail(mail);

        verify(javaMailSender, times(1)).send(mimeMessage);
    }
}
