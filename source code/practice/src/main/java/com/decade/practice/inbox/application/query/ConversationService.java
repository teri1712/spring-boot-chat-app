package com.decade.practice.inbox.application.query;

import com.decade.practice.inbox.dto.ConversationResponse;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ConversationService {

      List<ConversationResponse> list(UUID userId, Optional<Long> anchor) throws Throwable;

}