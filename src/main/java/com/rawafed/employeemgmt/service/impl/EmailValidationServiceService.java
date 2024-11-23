package com.rawafed.employeemgmt.service.impl;

import com.rawafed.employeemgmt.domain.EmailValidationOnErrorResponse;
import com.rawafed.employeemgmt.domain.EmailValidationResponse;
import com.rawafed.employeemgmt.service.IEmailValidationService;
import com.zerobounce.ZBValidateStatus;
import com.zerobounce.ZeroBounceSDK;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class EmailValidationServiceService implements IEmailValidationService {
    private final ZeroBounceSDK zeroBounceSDK;

    @Override
    public void validate(String email, Consumer<EmailValidationResponse> onSuccessConsumer,
                         Consumer<EmailValidationOnErrorResponse> onFailConsumer) {
        zeroBounceSDK.validate(
                email,
                null,
                zbValidateResponse -> {
                    val response = EmailValidationResponse.builder()
                            .valid(Objects.equals(zbValidateResponse.getStatus(), ZBValidateStatus.VALID))
                            .email(email)
                            .error(zbValidateResponse.getError())
                            .build();
                    onSuccessConsumer.accept(response);
                },
                errorResponse -> {
                    val response = EmailValidationOnErrorResponse.builder()
                            .description(String.join("", errorResponse.getErrors()))
                            .build();
                    onFailConsumer.accept(response);
                }
        );
    }
}
