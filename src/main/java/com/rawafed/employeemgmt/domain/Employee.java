package com.rawafed.employeemgmt.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Employee {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String department;
    private BigDecimal salary;
    private Boolean validEmail;
    private Boolean validDepartment;
    private String notes;
}
