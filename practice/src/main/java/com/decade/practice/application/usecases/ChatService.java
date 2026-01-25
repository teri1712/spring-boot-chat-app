package com.decade.practice.application.usecases;

import com.decade.practice.api.dto.ChatSnapshot;
import com.decade.practice.persistence.jpa.embeddables.ChatIdentifier;
import com.decade.practice.persistence.jpa.entities.Chat;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

public interface ChatService {


    void ensureExists(ChatIdentifier chatIdentifier);

    Chat createChat(ChatIdentifier identifier);

    Chat getOrCreateChat(ChatIdentifier identifier) throws NoSuchElementException;

    ChatSnapshot getSnapshot(ChatIdentifier chatIdentifier, UUID userId, int atVersion);

    List<Chat> listChat(UUID userId, Integer version, Optional<ChatIdentifier> offset, int limit);

    default Chat getOrCreateChat(UUID owner, UUID partner) throws NoSuchElementException {
        return getOrCreateChat(ChatIdentifier.from(owner, partner));
    }


}