package com.rawafed.employeemgmt.advice;

import com.rawafed.employeemgmt.api.model.Error;
import com.rawafed.employeemgmt.domain.exception.ResourceNotFoundException;
import com.rawafed.employeemgmt.domain.exception.UnprocessableInputException;
import com.rawafed.employeemgmt.domain.exception.VerificationRequiredException;
import com.rawafed.employeemgmt.domain.exception.common.ErrorEnum;
import jakarta.servlet.http.HttpServletRequest;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.MethodParameter;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.HttpServerErrorException.InternalServerError;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdvice;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Optional;

import static org.springframework.http.HttpStatus.*;

@ControllerAdvice
@Slf4j
public class ExceptionsHandlerAdvice implements RequestBodyAdvice {
    private static final ThreadLocal<Object> REQUEST_BODY_HOLDER = new ThreadLocal<>();

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Error> handleNotFoundExceptions(
            HttpServletRequest request, ResourceNotFoundException ex) {
        return createError(ErrorEnum.RESOURCE_NOT_FOUND_ERR, NOT_FOUND, request, ex);
    }

    @ExceptionHandler({HttpServerErrorException.class, InternalServerError.class})
    public ResponseEntity<Error> handleBadRequestExceptions(
            HttpServletRequest request, Exception ex) {
        return createError(ErrorEnum.GENERAL_ERR, INTERNAL_SERVER_ERROR, request, ex);
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, UnprocessableInputException.class})
    public ResponseEntity<Error> handleInvalidRequestExceptions(
            HttpServletRequest request, Exception ex) {
        return createError(ErrorEnum.INVALID_REQUEST_ERR, UNPROCESSABLE_ENTITY, request, ex);
    }

    @ExceptionHandler({DuplicateKeyException.class, DataIntegrityViolationException.class})
    public ResponseEntity<Error> handleDuplicateKeyException(
            HttpServletRequest request, Exception ex) {
        return createError(ErrorEnum.DUPLICATE_KEY_ERR, BAD_REQUEST, request, ex);
    }

    @ExceptionHandler(VerificationRequiredException.class)
    public ResponseEntity<Error> handleVerificationRequiredException(
            HttpServletRequest request, Exception ex) {
        return createError(ErrorEnum.UNSUPPORTED_TILL_VERIFIED_ERR, NOT_ACCEPTABLE, request, ex);
    }


    @SneakyThrows
    private ResponseEntity<Error> createError(ErrorEnum errorEnum, HttpStatus httpStatus, HttpServletRequest request, Exception ex) {
        final String path = request.getServletPath();
        final String method = request.getMethod();
        final String message = ex.getMessage();
        final String body = Optional.ofNullable(REQUEST_BODY_HOLDER.get()).orElse("").toString();

        log.error("Error occurred status: {} for {}:{}, body: {}, ex_message: {}",
                httpStatus, method, path, body, message);
        Error err = new Error(errorEnum.getCode(), errorEnum.getMessage(), message);
        return new ResponseEntity<>(err, httpStatus);
    }


    @Override
    public boolean supports(MethodParameter methodParameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public @NotNull HttpInputMessage beforeBodyRead(@NotNull HttpInputMessage inputMessage, @NotNull MethodParameter parameter, @NotNull Type targetType, @NotNull Class<? extends HttpMessageConverter<?>> converterType) throws IOException {
        REQUEST_BODY_HOLDER.remove();
        return inputMessage;
    }

    @Override
    public @NotNull Object afterBodyRead(@NotNull Object body, @NotNull HttpInputMessage inputMessage, @NotNull MethodParameter parameter, @NotNull Type targetType, @NotNull Class<? extends HttpMessageConverter<?>> converterType) {
        REQUEST_BODY_HOLDER.set(body);
        return body;
    }

    @Override
    public Object handleEmptyBody(Object body, @NotNull HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, @NotNull Class<? extends HttpMessageConverter<?>> converterType) {
        return body;
    }

}
