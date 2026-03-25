package com.decade.practice.chatorchestrator.application.ports.in;

import com.decade.practice.chatorchestrator.dto.ChatResponse;
import com.decade.practice.chatorchestrator.dto.DirectChatResponse;

import java.util.UUID;

public interface ChatService {

      ChatResponse createGroup(CreateGroupChatCommand command);

      DirectChatResponse getDirect(UUID callerId, UUID partnerId);

      ChatResponse find(String chatId, UUID userId);

}
