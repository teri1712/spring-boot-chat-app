package com.decade.practice.engagement.domain;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.UUID;
import java.util.stream.Stream;

@Embeddable
public record ChatCreators(
          UUID callerId,
          UUID partnerId
) implements Serializable {
      public Stream<UUID> members() {
            return Stream.of(callerId, partnerId).distinct();
      }
}