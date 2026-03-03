package com.decade.practice.engagement.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public record ChatPolicy(
          @Column(updatable = false)
          Integer maxParticipants
) {
}
