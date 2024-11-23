package com.rawafed.employeemgmt.domain.exception;

public class VerificationRequiredException extends AppException {
    public VerificationRequiredException() {
    }

    public VerificationRequiredException(String message) {
        super(message);
    }

    public VerificationRequiredException(String message, Throwable cause) {
        super(message, cause);
    }

    public VerificationRequiredException(Throwable cause) {
        super(cause);
    }

    public VerificationRequiredException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
