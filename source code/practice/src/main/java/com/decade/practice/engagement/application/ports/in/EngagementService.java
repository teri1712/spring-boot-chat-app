package com.decade.practice.engagement.application.ports.in;

import com.decade.practice.engagement.application.services.CreateGroupChatCommand;
import com.decade.practice.engagement.dto.ChatResponse;

import java.util.UUID;

public interface EngagementService {

    ChatResponse create(CreateGroupChatCommand command);

    ChatResponse getOrCreate(UUID callerId, UUID partnerId);

    void add(String chatId, UUID partnerId);
}
