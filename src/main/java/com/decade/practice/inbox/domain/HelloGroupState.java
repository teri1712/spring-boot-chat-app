package com.decade.practice.inbox.domain;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import java.util.UUID;
import java.util.stream.Stream;

@Getter
@SuperBuilder
@Jacksonized

public class HelloGroupState extends MessageState {

      private final UUID creator;

      @Override
      public Stream<UUID> getAllPartners() {
            return Stream.concat(super.getAllPartners(), Stream.of(creator));
      }
}
