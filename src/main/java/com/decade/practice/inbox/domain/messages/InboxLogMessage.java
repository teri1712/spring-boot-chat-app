package com.decade.practice.inbox.domain.messages;

import com.decade.practice.inbox.domain.ConversationInfo;
import com.decade.practice.inbox.domain.LogAction;
import com.decade.practice.inbox.dto.MessageStateResponse;

import java.util.UUID;

public record InboxLogMessage(
    Long sequenceNumber,
    String chatId,
    ConversationInfo info,
    Long revisionNumber,
    UUID senderId,
    UUID ownerId,
    LogAction action,
    MessageStateResponse messageState
) {
}
