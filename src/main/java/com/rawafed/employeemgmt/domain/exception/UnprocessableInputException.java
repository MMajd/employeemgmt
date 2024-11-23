package com.rawafed.employeemgmt.domain.exception;

public class UnprocessableInputException extends AppException {
    public UnprocessableInputException() {
    }

    public UnprocessableInputException(String message) {
        super(message);
    }

    public UnprocessableInputException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnprocessableInputException(Throwable cause) {
        super(cause);
    }

    public UnprocessableInputException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
