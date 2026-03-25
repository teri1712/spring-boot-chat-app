package com.decade.practice.inbox.domain.events;

import com.decade.practice.inbox.domain.MessageState;

import java.time.Instant;
import java.util.UUID;

public record MessageUpdated(Long id, UUID postingId, String chatId, UUID senderId, Instant at, MessageState currentState) {
}
