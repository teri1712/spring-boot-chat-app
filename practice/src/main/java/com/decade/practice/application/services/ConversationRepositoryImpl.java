package com.decade.practice.application.services;

import com.decade.practice.application.usecases.ChatService;
import com.decade.practice.application.usecases.ConversationRepository;
import com.decade.practice.domain.TypeEvent;
import com.decade.practice.domain.embeddables.ChatIdentifier;
import com.decade.practice.domain.entities.Chat;
import com.decade.practice.domain.entities.User;
import com.decade.practice.domain.repositories.UserRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ConversationRepositoryImpl implements ConversationRepository {

        private final UserRepository userRepo;
        private final ChatService chatService;

        public ConversationRepositoryImpl(
                UserRepository userRepo,
                ChatService chatService
        ) {
                this.userRepo = userRepo;
                this.chatService = chatService;
        }

        @Cacheable(
                cacheNames = "USER_ENTITIES_CACHE",
                key = "#username"
        )
        @Override
        public User getUser(String username) {
                return userRepo.findByUsername(username);
        }

        @Cacheable(
                cacheNames = "CHAT_ENTITIES_CACHE",
                key = "#id.toString()"
        )
        @Override
        public Chat getChat(ChatIdentifier id) {
                return chatService.getOrCreateChat(id);
        }

        @Cacheable(
                cacheNames = "Typing",
                key = "T(com.decade.practice.domain.TypeEvent).determineKey(#from,#chat)",
                cacheManager = "TYPE_EVENTS_CACHE_MANAGER",
                unless = "#result == null"
        )
        @Override
        public TypeEvent getType(ChatIdentifier chat, UUID from, boolean readOnly) {
                if (readOnly) {
                        return null;
                }
                return new TypeEvent(from, chat);
        }
}