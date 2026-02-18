package com.decade.practice.engagement.domain;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
public record ParticipantId(
        UUID userId,
        String chatId
) implements Serializable {
}
