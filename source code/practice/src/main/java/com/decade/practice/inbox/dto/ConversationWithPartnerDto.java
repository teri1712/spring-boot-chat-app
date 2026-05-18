package com.decade.practice.inbox.dto;

import java.time.Instant;
import java.util.List;

public record ConversationWithPartnerDto(
    String identifier,
    String roomName,
    String roomAvatar,
    Long revisionNumber,
    List<MessageStateWithPartnerDto> recents,
    Instant modifiedAt
) {
}
