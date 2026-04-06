package com.decade.practice.web;

import jakarta.annotation.Nullable;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.OptimisticLockException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.*;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
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
public class ExceptionControllerAdvice extends ResponseEntityExceptionHandler {

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
            log.debug("Concurrent update on the same resource", e);
      }

      @ExceptionHandler({ConstraintViolationException.class})
      @ResponseStatus(value = HttpStatus.BAD_REQUEST)
      public ProblemDetail handleBeanValidationException(ConstraintViolationException e) {
            String error = e.getConstraintViolations().stream().findFirst().map(ConstraintViolation::getMessage)
                      .orElse("Validation failure");
            return buildValidationProblem(e, error);
      }

      private static ProblemDetail buildValidationProblem(Exception ex, String detail) {
            log.warn("Validation failure", ex);
            ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST.value());
            pd.setDetail("Validation failure");
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