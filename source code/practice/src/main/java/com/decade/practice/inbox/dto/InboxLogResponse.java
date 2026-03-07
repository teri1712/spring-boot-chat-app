package com.decade.practice.inbox.dto;

import com.decade.practice.inbox.domain.LogAction;

import java.util.UUID;

public record InboxLogResponse(
          Long sequenceNumber,
          String chatId,
          String conversationName,
          String conversationAvatar,
          Long revisionNumber,
          UUID senderId,
          UUID ownerId,
          LogAction action,
          MessageStateResponse messageState
) {
}
