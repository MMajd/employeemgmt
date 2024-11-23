package com.rawafed.employeemgmt.service.impl;

import com.rawafed.employeemgmt.domain.event.EmployeeDepartmentVerificationEvent;
import com.rawafed.employeemgmt.service.IDepartmentValidationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
@Slf4j
public class DepartmentValidationService implements IDepartmentValidationService {

    @Override
    public void validate(EmployeeDepartmentVerificationEvent event, Consumer<Object> onValidationConsumer, Consumer<Object> onErrorConsumer) {
        log.info("Received employee department validation event: {}", event);
        try {
            Thread.sleep(500);
            onValidationConsumer.accept(null);
        } catch (InterruptedException e) {
            onErrorConsumer.accept(e);
        }
        log.info("Employee department validation finished successfully");
    }
}
