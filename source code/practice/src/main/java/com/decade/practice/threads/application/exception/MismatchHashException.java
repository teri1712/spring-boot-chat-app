package com.decade.practice.threads.application.exception;

import com.decade.practice.threads.domain.HashValue;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class MismatchHashException extends RuntimeException {
    private final HashValue expected;
    private final HashValue received;

    @Override
    public String getMessage() {
        return "Expected version: " + expected + ", received version: " + received;
    }
}
