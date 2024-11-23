package com.rawafed.employeemgmt.service;


import com.rawafed.employeemgmt.domain.event.EmployeeDepartmentVerificationEvent;

import java.util.function.Consumer;

public interface IDepartmentValidationService {
    void validate(EmployeeDepartmentVerificationEvent event,
                  Consumer<Object> onValidationConsumer, Consumer<Object> onErrorConsumer);
}
