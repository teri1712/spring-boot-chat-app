package com.decade.practice.application.usecases;

import com.decade.practice.dto.ChatDetails;
import com.decade.practice.dto.ChatSnapshot;
import com.decade.practice.persistence.jpa.entities.Chat;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChatService {

    ChatDetails createChat(String chatId, UUID userId, String roomName, Integer iconId, UUID withPartner);

    ChatDetails getDetails(String chatId, UUID userId);

    ChatSnapshot getSnapshot(String chatId, UUID userId, int atVersion);

    List<Chat> listChat(UUID userId, Integer version, Optional<String> offset, int limit);

}