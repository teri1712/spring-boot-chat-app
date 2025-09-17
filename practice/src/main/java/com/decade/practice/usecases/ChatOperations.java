package com.decade.practice.usecases;

import com.decade.practice.models.domain.ChatSnapshot;
import com.decade.practice.models.domain.embeddable.ChatIdentifier;
import com.decade.practice.models.domain.entity.Chat;
import com.decade.practice.models.domain.entity.User;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

public interface ChatOperations {
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