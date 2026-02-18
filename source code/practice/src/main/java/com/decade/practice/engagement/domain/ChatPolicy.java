package com.decade.practice.engagement.domain;

import jakarta.persistence.Embeddable;

@Embeddable
public record ChatPolicy(
        Integer maxParticipants
) {
}
