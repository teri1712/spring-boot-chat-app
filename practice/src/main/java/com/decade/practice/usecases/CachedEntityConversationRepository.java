package com.decade.practice.usecases;

import com.decade.practice.data.repositories.UserRepository;
import com.decade.practice.models.domain.TypeEvent;
import com.decade.practice.models.domain.embeddable.ChatIdentifier;
import com.decade.practice.models.domain.entity.Chat;
import com.decade.practice.models.domain.entity.User;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CachedEntityConversationRepository implements ConversationRepository {


        private final UserRepository userRepo;
        private final ChatOperations chatOperations;

        public CachedEntityConversationRepository(
                UserRepository userRepo,
                ChatOperations chatOperations
        ) {
                this.userRepo = userRepo;
                this.chatOperations = chatOperations;
        }

        @Cacheable(
                cacheNames = "USER_ENTITIES_CACHE",
                key = "#username"
        )
        @Override
        public User getUser(String username) {
                return userRepo.getByUsername(username);
        }

        @Cacheable(
                cacheNames = "CHAT_ENTITIES_CACHE",
                key = "#id.toString()"
        )
        @Override
        public Chat getChat(ChatIdentifier id) {
                return chatOperations.getOrCreateChat(id);
        }

        @Cacheable(
                cacheNames = "Typing",
                key = "T(com.decade.practice.models.domain.TypeEvent).determineKey(#from,#chat)",
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