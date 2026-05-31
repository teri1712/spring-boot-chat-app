package com.decade.practice.engagement.api;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ChatIdentifierUniqueException extends RuntimeException {
      private final String id;

      @Override
      public String getMessage() {
            return "Chat identifier already exists for " + id;
      }
}
