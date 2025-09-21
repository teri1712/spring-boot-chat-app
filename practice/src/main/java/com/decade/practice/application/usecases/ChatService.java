package com.decade.practice.application.usecases;

import com.decade.practice.domain.ChatSnapshot;
import com.decade.practice.domain.embeddables.ChatIdentifier;
import com.decade.practice.domain.entities.Chat;
import com.decade.practice.domain.entities.User;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

public interface ChatService {
        int CONVERSATION_LIMIT = 20;

        void createChat(ChatIdentifier identifier);

        Chat getOrCreateChat(ChatIdentifier identifier) throws NoSuchElementException;

        default Chat getOrCreateChat(UUID owner, UUID partner) throws NoSuchElementException {
                return getOrCreateChat(ChatIdentifier.from(owner, partner));
        }

        List<Chat> listChat(User owner, Integer version, Chat offset, int limit);

        default List<Chat> listChat(User owner, Integer version, Chat offset) {
                return listChat(owner, version, offset, CONVERSATION_LIMIT);
        }

        default List<Chat> listChat(User owner, Integer version) {
                return listChat(owner, version, null, CONVERSATION_LIMIT);
        }

        default List<Chat> listChat(User owner) {
                return listChat(owner, null, null, CONVERSATION_LIMIT);
        }

        ChatSnapshot getSnapshot(Chat chat, User owner, int atVersion);
}