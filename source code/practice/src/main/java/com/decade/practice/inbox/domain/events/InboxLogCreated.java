package com.decade.practice.inbox.domain.events;

import com.decade.practice.inbox.domain.LogAction;
import com.decade.practice.inbox.domain.MessageState;

import java.util.UUID;

public record InboxLogCreated(
          Long sequenceId,
          String chatId,
          UUID senderId,
          UUID ownerId,
          LogAction action,
          MessageState messageState
) {
}
