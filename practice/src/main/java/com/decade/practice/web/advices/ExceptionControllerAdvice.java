package com.decade.practice.web.advices;

import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.OptimisticLockException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.NoSuchElementException;

@RestControllerAdvice
public class ExceptionControllerAdvice extends ResponseEntityExceptionHandler {

      @ExceptionHandler({
            EntityNotFoundException.class,
            NoSuchElementException.class,
            NullPointerException.class
      })
      @ResponseStatus(value = HttpStatus.NOT_FOUND)
      @MessageExceptionHandler({
            EntityNotFoundException.class,
            NoSuchElementException.class,
            NullPointerException.class
      })
      public void handleNoElement(Exception e) {
            e.printStackTrace();
      }

      @ExceptionHandler(OptimisticLockException.class)
      @ResponseStatus(value = HttpStatus.CONFLICT)
      public void handleLockException() {
      }

      @Override
      protected ResponseEntity<Object> handleHandlerMethodValidationException(
            HandlerMethodValidationException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
      ) {
            return ResponseEntity
                  .badRequest()
                  .body(
                        ex.getAllErrors()
                              .get(0).getDefaultMessage()
                  );
      }

      @Override
      protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
      ) {
            return ResponseEntity
                  .badRequest()
                  .body(ex.getMessage());
      }
}