package com.decade.practice.inbox.dto;


import java.time.Instant;
import java.util.List;
import java.util.Set;

public record ConversationResponse(
          String identifier,
          String roomName,
          String roomAvatar,
          Long hashValue,
          Set<PartnerResponse> seenBy,
          List<MessagePreviewResponse> messagePreviews,
          Instant modifiedAt
) {


}
