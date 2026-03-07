package com.decade.practice.inbox.application.query;

import com.decade.practice.inbox.dto.ChatEventResponse;

import java.util.UUID;

public interface ChatEventService {
      ChatEventResponse find(UUID idempotentKey);
}
