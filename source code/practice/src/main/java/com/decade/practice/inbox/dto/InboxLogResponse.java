package com.decade.practice.inbox.dto;

import com.decade.practice.inbox.domain.LogAction;

import java.util.UUID;

public record InboxLogResponse(
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
