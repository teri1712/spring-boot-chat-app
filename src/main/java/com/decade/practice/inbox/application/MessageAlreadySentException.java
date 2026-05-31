package com.decade.practice.inbox.application;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@AllArgsConstructor
@Getter
public class MessageAlreadySentException extends RuntimeException {
      private final UUID idempotentId;
}
