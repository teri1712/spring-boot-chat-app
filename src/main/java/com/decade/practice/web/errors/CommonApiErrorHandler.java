package com.decade.practice.web.errors;

import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.OptimisticLockException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.*;
import org.springframework.validation.method.MethodValidationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.NoSuchElementException;

@Slf4j
@RestControllerAdvice
public class CommonApiErrorHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({EntityNotFoundException.class, NoSuchElementException.class,})
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    ProblemDetail handleNoEntity(Exception e) {
        log.warn("Entity not found", e);
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.NOT_FOUND.value());
        pd.setTitle("The requested resource not found");
        return pd;
    }

    @ExceptionHandler(OptimisticLockException.class)
    @ResponseStatus(value = HttpStatus.CONFLICT)
    ProblemDetail handleLockException(OptimisticLockException e) {
        log.debug("Conflict encountered on the resource", e);
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.CONFLICT.value());
        pd.setTitle("Conflict encountered on the resource, Please retry later");
        return pd;
    }

    @ExceptionHandler({ConstraintViolationException.class})
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    ProblemDetail handleBeanValidationException(ConstraintViolationException e) {
        String error = e.getConstraintViolations().stream().findFirst().map(ConstraintViolation::getMessage)
            .orElse("Validation failure");
        return buildValidationProblem(e, error);
    }

    private static ProblemDetail buildValidationProblem(Exception ex, String detail) {
        log.warn("Validation failure", ex);
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST.value());
        pd.setDetail(detail);
        return pd;
    }

    @Override
    protected @Nullable ResponseEntity<Object> handleHandlerMethodValidationException(HandlerMethodValidationException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        String error = ex.getAllErrors()
            .stream()
            .findFirst().map(MessageSourceResolvable::getDefaultMessage).orElse("Validation failure");
        return new ResponseEntity<>(buildValidationProblem(ex, error), HttpStatus.BAD_REQUEST);
    }

    @Override
    protected @Nullable ResponseEntity<Object> handleMethodValidationException(MethodValidationException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        String error = ex.getAllErrors().stream().findFirst().map(MessageSourceResolvable::getDefaultMessage).orElse("Validation failure");
        return new ResponseEntity<>(buildValidationProblem(ex, error), HttpStatus.BAD_REQUEST);
    }
}