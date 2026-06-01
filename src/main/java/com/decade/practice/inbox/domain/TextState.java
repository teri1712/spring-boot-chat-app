package com.decade.practice.inbox.domain;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

@Getter
@SuperBuilder
@Jacksonized

public class TextState extends MessageState {
      private final String content;
}
