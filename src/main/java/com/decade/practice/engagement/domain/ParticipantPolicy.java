package com.decade.practice.engagement.domain;

import jakarta.persistence.Embeddable;

@Embeddable
public record ParticipantPolicy(
        boolean write,
        boolean read
) {
}
