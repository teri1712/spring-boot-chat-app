package com.decade.practice.chat.application.ports.in;

import com.decade.practice.chat.dto.ChatResponse;
import com.decade.practice.chat.dto.DirectChatResponse;

import java.util.UUID;

public interface ChatService {

      ChatResponse createGroup(CreateGroupChatCommand command);

      DirectChatResponse getDirect(UUID callerId, UUID partnerId);

      ChatResponse find(String chatId, UUID userId);

}
