package com.decade.practice.inbox.dto;

import com.decade.practice.inbox.domain.LogAction;

public record InboxLogMessageWithPartnerDto(
    Long sequenceNumber,
    String chatId,
    String roomName,
    String roomAvatar,
    Long revisionNumber,
    PartnerResponse sender,
    LogAction action,
    MessageStateWithPartnerDto messageState
) {
}
