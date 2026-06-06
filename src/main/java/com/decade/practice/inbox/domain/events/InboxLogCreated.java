package com.decade.practice.inbox.domain.events;

import com.decade.practice.inbox.domain.LogAction;

import java.util.UUID;

public record InboxLogCreated(
    Long sequenceId,
    Long conversationId,
    Long messageId,
    UUID senderId,
    UUID ownerId,
    LogAction action) {
}
