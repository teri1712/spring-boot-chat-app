package com.decade.practice.inbox.domain.messages;

import com.decade.practice.inbox.domain.LogAction;
import com.decade.practice.inbox.dto.MessageStateResponse;
import com.decade.practice.inbox.dto.PartnerResponse;

import java.util.UUID;

public record InboxLogMessage(
          Long sequenceNumber,
          String chatId,
          String roomName,
          String roomAvatar,
          Long revisionNumber,
          PartnerResponse sender,
          UUID ownerId,
          LogAction action,
          MessageStateResponse messageState
) {
}
