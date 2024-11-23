package com.rawafed.employeemgmt.service;

import com.rawafed.employeemgmt.domain.EmailValidationOnErrorResponse;
import com.rawafed.employeemgmt.domain.EmailValidationResponse;

import java.util.function.Consumer;

public interface IEmailValidationService {
    void validate(String email, Consumer<EmailValidationResponse> onSuccessConsumer, Consumer<EmailValidationOnErrorResponse> onFailConsumer);
}
