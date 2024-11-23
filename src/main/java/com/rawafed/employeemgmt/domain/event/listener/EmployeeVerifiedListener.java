package com.rawafed.employeemgmt.domain.event.listener;


import com.rawafed.employeemgmt.domain.Mail;
import com.rawafed.employeemgmt.domain.event.EmployeeDepartmentVerificationEvent;
import com.rawafed.employeemgmt.domain.event.EmployeeVerifiedEvent;
import com.rawafed.employeemgmt.service.IDepartmentValidationService;
import com.rawafed.employeemgmt.service.IEmployeeService;
import com.rawafed.employeemgmt.service.IMailService;
import jakarta.mail.internet.InternetAddress;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmployeeVerifiedListener {
    private final IDepartmentValidationService departmentValidationService;
    private final IEmployeeService employeeService;
    private final IMailService mailService;

    @Value("${spring.mail.username}")
    private String mailbox;

    @Value("${app.mail.from}")
    private String mailboxName;

    @Async
    @EventListener
    public void sendVerificationMail(EmployeeVerifiedEvent event) {
        val departValidationEvent = EmployeeDepartmentVerificationEvent.builder()
                .email(event.getEmail())
                .department("d1")
                .build();

        departmentValidationService.validate(departValidationEvent, (o) -> {
            log.info("Validation was successful");
            val employee = employeeService.find(event.getEmail());
            employee.setValidDepartment(Boolean.TRUE);
            employeeService.internalUpdate(employee);
            try {
                val fromMail = String.valueOf(new InternetAddress(mailbox, mailboxName));

                log.info("Sender email: {} ", fromMail);

                mailService.sendMail(Mail.builder()
                        .from(fromMail)
                        .to(employee.getEmail())
                        .subject("You're good to go")
                        .body("Dear user, \nwe are just letting you to know that your account has been verified and you can use it now! \nBR, Rawafedtech Team.")
                        .build());
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }, (e) -> {
            log.info("Something went wrong while doing department validation");
        });
    }
}
