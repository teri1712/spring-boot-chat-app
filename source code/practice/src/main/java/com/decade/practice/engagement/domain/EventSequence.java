package com.decade.practice.engagement.domain;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Version;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Embeddable
class EventSequence {
      @Version
      private Integer eventVersion;

      @Setter(AccessLevel.PACKAGE)
      private UUID lastEventId;

}