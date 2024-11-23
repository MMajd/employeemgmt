package com.rawafed.employeemgmt.domain.event.listener;

import com.rawafed.employeemgmt.domain.EmailValidationOnErrorResponse;
import com.rawafed.employeemgmt.domain.EmailValidationResponse;
import com.rawafed.employeemgmt.domain.event.EmployeeBaseEvent;
import com.rawafed.employeemgmt.domain.event.EmployeeEmailValidationEvent;
import com.rawafed.employeemgmt.domain.event.EmployeeRegistrationEvent;
import com.rawafed.employeemgmt.domain.event.EmployeeVerifiedEvent;
import com.rawafed.employeemgmt.domain.presistent.model.EventModel;
import com.rawafed.employeemgmt.domain.presistent.model.EventObjectEmbedding;
import com.rawafed.employeemgmt.domain.presistent.repo.EventRepository;
import com.rawafed.employeemgmt.service.IEmailValidationService;
import com.rawafed.employeemgmt.service.IEmployeeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Consumer;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmployeeEmailValidationListener {
    private final IEmailValidationService validationService;
    private final IEmployeeService employeeService;
    private final EventRepository eventRepository;
    private final ApplicationEventPublisher eventPublisher;


    @EventListener
    public void registrationListener(EmployeeRegistrationEvent event) {
        validateEmail(event);
    }

    @EventListener
    public void validateEmailListener(EmployeeEmailValidationEvent event) {
        validateEmail(event);
    }

    @Async
    public void validateEmail(EmployeeBaseEvent event) {
        log.info("Started email validation with data: {}", event);
        validationService.validate(event.getEmail(), getOnValidationCallback(), getOnErrorCallback());
        log.info("Email validation finished");
    }

    Consumer<EmailValidationResponse> getOnValidationCallback() {
        return (response) -> {
            log.info("Received email validation response for : {}", response.getEmail());
            val employee = employeeService.find(response.getEmail());

            if (response.isValid()) {
                log.info("Email: {} is valid one", response.getEmail());
                employee.setValidEmail(response.isValid());
                log.info("Will start updating employee email validation info with response values: {}", response);
                employee.setValidEmail(Boolean.TRUE);
                employeeService.internalUpdate(employee);
                eventRepository.deleteByEmployeeEmailAndEventType(response.getEmail(),
                        EmployeeEmailValidationEvent.class.getSimpleName());
                eventPublisher.publishEvent(EmployeeVerifiedEvent.builder().email(response.getEmail()).build());
            } else {
                log.info("Errors occurred during the validation: {}", response.getError());
                employee.setNotes(String.join("\n", List.of(employee.getNotes(), response.getError())));
            }

        };
    }

    private Consumer<EmailValidationOnErrorResponse> getOnErrorCallback() {
        return (errResponse) -> {
            log.info("Something went wrong during calling the validation service: {}", errResponse.getDescription());
            val event = EmployeeEmailValidationEvent
                    .builder()
                    .email(errResponse.getEmail())
                    .build();

            val validation = EventModel.builder()
                    .employeeEmail(errResponse.getEmail())
                    .eventType(event.getClass().getSimpleName())
                    .event(EventObjectEmbedding.builder()
                            .email(event.getEmail())
                            .build())
                    .build();

            log.info("Saving event to later use {}", event);
            eventRepository.save(validation);
        };
    }
}

