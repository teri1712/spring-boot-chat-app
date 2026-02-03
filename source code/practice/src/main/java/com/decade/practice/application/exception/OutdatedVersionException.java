package com.decade.practice.application.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class OutdatedVersionException extends RuntimeException {
    private final int expectedVersion;
    private final int receivedVersion;

    @Override
    public String getMessage() {
        return "Expected version: " + expectedVersion + ", received version: " + receivedVersion;
    }
}
