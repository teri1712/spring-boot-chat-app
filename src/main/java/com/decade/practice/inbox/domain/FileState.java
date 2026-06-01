package com.decade.practice.inbox.domain;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

@Getter
@SuperBuilder
@Jacksonized

public class FileState extends MessageState {

      private final String filename;
      private final Integer size;
      private final String uri;

}
