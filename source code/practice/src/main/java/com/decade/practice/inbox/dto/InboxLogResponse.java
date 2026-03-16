package com.decade.practice.inbox.dto;

import com.decade.practice.inbox.domain.LogAction;
import com.decade.practice.users.api.UserInfo;

import java.util.UUID;

public record InboxLogResponse(
          Long sequenceNumber,
          UUID postingId,
          String chatId,
          String roomName,
          String roomAvatar,
          Long revisionNumber,
          UserInfo sender,
          UUID ownerId,
          LogAction action,
          MessageStateResponse messageState
) {
}
