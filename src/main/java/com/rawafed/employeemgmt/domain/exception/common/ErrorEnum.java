package com.rawafed.employeemgmt.domain.exception.common;

public enum ErrorEnum implements IErrorDescriptor {
    GENERAL_ERR("EMGM001", "Something went wrong, please try again later"),
    RESOURCE_NOT_FOUND_ERR("EMGM002", "Resource not found in database"),
    DUPLICATE_KEY_ERR("EMGM003", "Email already exist in database"),
    INVALID_REQUEST_ERR("EMGM004", "Incorrect request body, parameters or variables."),
    UNSUPPORTED_TILL_VERIFIED_ERR("EMGM005", "Operation is unsupported till employee data is verified");

    private final String code;
    private final String message;

    ErrorEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public String getCode() {
        return code;
    }
}
