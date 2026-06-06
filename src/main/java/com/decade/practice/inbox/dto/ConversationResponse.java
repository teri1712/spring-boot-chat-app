package com.decade.practice.inbox.dto;


import com.decade.practice.inbox.domain.ConversationInfo;

import java.time.Instant;
import java.util.List;

public record ConversationResponse(
    String identifier,
    ConversationInfo info,
    Long revisionNumber,
    List<MessageStateResponse> recents,
    Instant modifiedAt
) {


}
