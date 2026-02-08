package com.decade.practice.application.usecases;

import com.decade.practice.dto.ChatDetails;

import java.util.UUID;

public interface CreateChatCommandHandler {
    ChatDetails create(UUID requesterId, UUID partnerId);
}
