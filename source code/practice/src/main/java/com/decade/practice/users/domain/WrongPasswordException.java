package com.decade.practice.users.domain;

import org.springframework.security.access.AccessDeniedException;

public class WrongPasswordException extends AccessDeniedException {
    public WrongPasswordException() {
        super("Passwords do not match");
    }
}
