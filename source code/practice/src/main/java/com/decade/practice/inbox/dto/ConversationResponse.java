package com.decade.practice.inbox.dto;


import java.time.Instant;
import java.util.List;

public record ConversationResponse(
          String identifier,
          String roomName,
          String roomAvatar,
          Long revisionNumber,
          List<MessageStateResponse> recents,
          Instant modifiedAt
) {


}
