package com.decade.practice.resources.files.adapter;

import com.decade.practice.resources.files.api.FileIntegrityException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class FIleControllerAdvice {

      @ExceptionHandler(FileIntegrityException.class)
      @ResponseStatus(value = HttpStatus.UNPROCESSABLE_ENTITY)
      public ProblemDetail handleFileIntegrityException(FileIntegrityException ex) {
            log.warn("File integrity violation", ex);
            ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.UNPROCESSABLE_ENTITY);
            pd.setTitle("File integrity violation");
            return pd;
      }
}
