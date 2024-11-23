package com.rawafed.employeemgmt.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmailValidationOnErrorResponse {
    private String email;
    private String description;
}
