package com.decade.practice.inbox.domain.messages;

import com.decade.practice.inbox.domain.LogAction;
import com.decade.practice.inbox.dto.MessageStateResponse;
import com.decade.practice.users.api.UserInfo;

import java.util.UUID;

public record InboxLogMessage(
          Long sequenceNumber,
          String chatId,
          UUID postingId,
          String roomName,
          String roomAvatar,
          Long revisionNumber,
          UserInfo sender,
          UUID ownerId,
          LogAction action,
          MessageStateResponse messageState
) {
}
