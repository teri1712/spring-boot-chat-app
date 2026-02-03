package com.decade.practice.api.web.advices;

import com.decade.practice.application.exception.OutdatedVersionException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.OptimisticLockException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.NoSuchElementException;

@Slf4j
@RestControllerAdvice
public class ExceptionControllerAdvice {

    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    @ExceptionHandler({
            EntityNotFoundException.class,
            NoSuchElementException.class,
    })
    @MessageExceptionHandler({
            EntityNotFoundException.class,
            NoSuchElementException.class,
    })
    public void handleNoEntity(Exception e) {
        log.warn("Entity not found", e);
    }

    @ExceptionHandler(OptimisticLockException.class)
    @ResponseStatus(value = HttpStatus.CONFLICT)
    public void handleLockException(Exception e) {
        log.warn("Concurrent update on the same resource", e);
    }

    @ExceptionHandler(OutdatedVersionException.class)
    @ResponseStatus(value = HttpStatus.CONFLICT)
    public ProblemDetail handleOutdatedVersionException(OutdatedVersionException e) {
        log.warn("Version mismatched", e);
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.CONFLICT.value());
        pd.setDetail(e.getMessage());
        pd.setTitle("Version mismatched");
        return pd;
    }

    @ExceptionHandler({ConstraintViolationException.class})
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ProblemDetail handleBeanValidationException(Exception e) {
        log.warn("Validation failure", e);
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST.value());
        pd.setDetail(e.getMessage());
        pd.setTitle("Validation failure");
        return pd;
    }

}