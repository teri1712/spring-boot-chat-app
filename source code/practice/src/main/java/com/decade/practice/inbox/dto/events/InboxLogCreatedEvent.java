package com.decade.practice.inbox.dto.events;

import com.decade.practice.inbox.domain.LogAction;
import com.decade.practice.inbox.dto.MessageStateResponse;

import java.util.UUID;

public record InboxLogCreatedEvent(
          Long sequenceId,
          String chatId,
          String roomNameSnapshot,
          String roomAvatarSnapshot,
          UUID senderId,
          UUID ownerId,
          LogAction action,
          MessageStateResponse messageState
) {
}
