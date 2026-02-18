package com.decade.practice.threads.adapter;

import com.decade.practice.threads.application.exception.MismatchHashException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ThreadsExceptionHandler {

    @ExceptionHandler(MismatchHashException.class)
    @ResponseStatus(value = HttpStatus.CONFLICT)
    public ProblemDetail handleOutdatedVersionException(MismatchHashException e) {
        log.warn("Hash mismatched", e);
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.CONFLICT.value());
        pd.setDetail(e.getMessage());
        pd.setTitle("Hash mismatched");
        return pd;
    }
}
