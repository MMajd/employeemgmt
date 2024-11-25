package com.rawafed.employeemgmt.service.impl;

import com.rawafed.employeemgmt.domain.Mail;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;

import java.time.Duration;

import static io.github.resilience4j.circuitbreaker.CircuitBreakerConfig.SlidingWindowType.COUNT_BASED;
import static java.util.stream.IntStream.range;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class MailServiceTest {
    @Mock
    MimeMessage mimeMessage;

    @Spy
    CircuitBreaker circuitBreaker;

    @MockBean
    CircuitBreakerRegistry circuitBreakerRegistry;

    @MockBean
    JavaMailSender javaMailSender;

    @Autowired
    @SpyBean
    MailService mailService;

    Mail mail;

    @BeforeEach
    void setUp() {
        mail = Mail.builder()
                .from("sender@example.com")
                .to("recipient@example.com")
                .subject("Test Subject")
                .body("Test Body")
                .build();
        circuitBreaker = CircuitBreaker.of("mailgun-service", CircuitBreakerConfig
                .custom()
                .minimumNumberOfCalls(4)
                .failureRateThreshold(50)
                .slidingWindowSize(4)
                .permittedNumberOfCallsInHalfOpenState(2)
                .automaticTransitionFromOpenToHalfOpenEnabled(true)
                .waitDurationInOpenState(Duration.ofMillis(100))
                .slidingWindowType(COUNT_BASED)
                .build());

        System.out.println(">> " + circuitBreaker.getCircuitBreakerConfig());

        when(circuitBreakerRegistry.circuitBreaker("mailgun-service")).thenReturn(circuitBreaker);
        doReturn(mimeMessage).when(javaMailSender).createMimeMessage();
    }

    @Test
    void testCircuitBreakerInClosedState() {
        MailException mockMailException = mock(MailException.class);
        doThrow(mockMailException).when(javaMailSender).send(mimeMessage);

        Assertions.assertEquals(CircuitBreaker.State.CLOSED, circuitBreaker.getState());

        mailService.sendMail(mail);
        doNothing().when(javaMailSender).send(mimeMessage);
        mailService.sendMail(mail);

        Assertions.assertEquals(CircuitBreaker.State.CLOSED, circuitBreaker.getState());
        Assertions.assertEquals(1, circuitBreaker.getMetrics().getNumberOfFailedCalls());
        verify(mailService, times(1)).sendEmailFallback(any(), any());
        verify(mailService, times(2)).sendMail(mail);
    }

    @Test
    void testCircuitBreakerInOpenStateTriggersFallback() {
        final int noOfCalls = 4;
        MailException mockMailException = mock(MailException.class);
        doThrow(mockMailException).when(javaMailSender).send(mimeMessage);

        range(0, noOfCalls).forEach(
                x -> mailService.sendMail(mail)
        );

        Assertions.assertEquals(CircuitBreaker.State.OPEN, circuitBreaker.getState());
        verify(mailService, times(noOfCalls)).sendEmailFallback(any(), any());
    }


    @Test
    void testCircuitBreakerInHalfOpenState() throws Exception {
        MailException mockMailException = mock(MailException.class);
        doThrow(mockMailException).when(javaMailSender).send(mimeMessage);

        range(0, 4).forEach(
                x -> mailService.sendMail(mail)
        );

        Thread.sleep(101);

        Assertions.assertEquals(CircuitBreaker.State.HALF_OPEN, circuitBreaker.getState());

        doNothing().when(javaMailSender).send(mimeMessage);

        range(0, 2).forEach(
                x -> mailService.sendMail(mail)
        );

        System.out.println(">> State : " + circuitBreaker.getState());
        Assertions.assertEquals(CircuitBreaker.State.CLOSED, circuitBreaker.getState());
    }
}