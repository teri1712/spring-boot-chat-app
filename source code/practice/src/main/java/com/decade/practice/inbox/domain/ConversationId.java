package com.decade.practice.inbox.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.util.UUID;

@Embeddable
public record ConversationId(
          String chatId,
          @Column(nullable = false)
          UUID ownerId

) {
}
