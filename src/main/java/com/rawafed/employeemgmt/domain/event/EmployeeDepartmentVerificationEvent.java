package com.rawafed.employeemgmt.domain.event;

import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Value
@SuperBuilder
public class EmployeeDepartmentVerificationEvent extends EmployeeBaseEvent {
    String department;
}
