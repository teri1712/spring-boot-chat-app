package com.decade.practice.inbox.application.query;

import com.decade.practice.inbox.dto.PostingResponse;

import java.util.UUID;

public interface ChatEventService {
      PostingResponse find(UUID idempotentKey);
}
