package com.decade.practice.engagement.application.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ChatIdentifierUniqueException extends RuntimeException {
    private final String id;
}
