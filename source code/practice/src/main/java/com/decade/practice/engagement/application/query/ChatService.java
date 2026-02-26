package com.decade.practice.engagement.application.query;

import com.decade.practice.engagement.dto.ChatResponse;

import java.util.UUID;

public interface ChatService {

      ChatResponse find(String chatId, UUID userId);
}