package com.decade.practice.engagement.application.query;

import com.decade.practice.engagement.dto.ChatResponse;

import java.util.UUID;

public interface ChatService {

    ChatResponse getDetails(String chatId, UUID userId);

}