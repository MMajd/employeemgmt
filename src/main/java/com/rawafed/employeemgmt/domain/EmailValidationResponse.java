package com.rawafed.employeemgmt.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmailValidationResponse {
    /**
     * zerobounce response example
     * {
     * "email": "example@example.com",
     * "status": "valid",
     * "reasons": [],
     * "smtp_check": true,
     * "dns_check": true,
     * "mx_record_found": true,
     * "domain_exists": true,
     * }
     */
    private String email;
    private boolean valid;
    private String error;
}
