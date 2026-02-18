package com.decade.practice.threads.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.util.UUID;

@Embeddable
public record ChatHistoryId(
        String chatId,
        @Column(nullable = false)
        UUID ownerId

) {
}
