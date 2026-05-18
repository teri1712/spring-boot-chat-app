package com.decade.practice.inbox.dto;

import com.decade.practice.inbox.domain.LogAction;

public record InboxLogWithPartnerDto(
    Long sequenceNumber,
    String chatId,
    String roomName,
    String roomAvatar,
    Long revisionNumber,
    PartnerResponse sender,
    PartnerResponse owner,
    LogAction action,
    MessageStateWithPartnerDto messageState
) {
}
