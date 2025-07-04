package com.decade.practice.websocket;

import com.decade.practice.core.ChatOperations;
import com.decade.practice.database.repository.UserRepository;
import com.decade.practice.model.domain.TypeEvent;
import com.decade.practice.model.domain.embeddable.ChatIdentifier;
import com.decade.practice.model.domain.entity.Chat;
import com.decade.practice.model.domain.entity.User;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class WsCachedEntityRepository implements WsEntityRepository {

      private final UserRepository userRepo;
      private final ChatOperations chatOperations;

      public WsCachedEntityRepository(
            UserRepository userRepo,
            ChatOperations chatOperations
      ) {
            this.userRepo = userRepo;
            this.chatOperations = chatOperations;
      }

      @Cacheable(
            cacheNames = CacheConstants.USER_KEYSPACE,
            key = "#username"
      )
      @Override
      public User getUser(String username) {
            return userRepo.getByUsername(username);
      }

      @Cacheable(
            cacheNames = CacheConstants.CHAT_KEYSPACE,
            key = "#id.toString()"
      )
      @Override
      public Chat getChat(ChatIdentifier id) {
            return chatOperations.getOrCreateChat(id);
      }

      @Cacheable(
            cacheNames = CacheConstants.TYPE_KEYSPACE,
            key = "T(com.decade.practice.model.domain.TypeEvent).determineKey(#from,#chat)",
            cacheManager = CacheConstants.TYPE_REPOSITORY_CACHE_MANAGER,
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