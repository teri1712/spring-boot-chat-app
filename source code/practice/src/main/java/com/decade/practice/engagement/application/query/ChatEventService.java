package com.decade.practice.engagement.application.query;

import com.decade.practice.engagement.dto.ChatEventResponse;

import java.util.UUID;

public interface ChatEventService {
      ChatEventResponse find(UUID idempotentKey);
}
