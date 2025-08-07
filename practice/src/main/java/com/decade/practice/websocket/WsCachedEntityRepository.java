package com.decade.practice.websocket;

import com.decade.practice.database.repository.UserRepository;
import com.decade.practice.entities.domain.TypeEvent;
import com.decade.practice.entities.domain.embeddable.ChatIdentifier;
import com.decade.practice.entities.domain.entity.Chat;
import com.decade.practice.entities.domain.entity.User;
import com.decade.practice.usecases.core.ChatOperations;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static com.decade.practice.websocket.CacheConfiguration.TYPE_REPOSITORY_CACHE_MANAGER;

@Component
public class WsCachedEntityRepository implements WsEntityRepository {

      public static final String USER_KEYSPACE = "USER_ENTITIES_CACHE";
      public static final String CHAT_KEYSPACE = "CHAT_ENTITIES_CACHE";
      public static final String TYPE_KEYSPACE = "Typing";

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
            cacheNames = USER_KEYSPACE,
            key = "#username"
      )
      @Override
      public User getUser(String username) {
            return userRepo.getByUsername(username);
      }

      @Cacheable(
            cacheNames = CHAT_KEYSPACE,
            key = "#id.toString()"
      )
      @Override
      public Chat getChat(ChatIdentifier id) {
            return chatOperations.getOrCreateChat(id);
      }

      @Cacheable(
            cacheNames = TYPE_KEYSPACE,
            key = "T(com.decade.practice.entities.domain.TypeEvent).determineKey(#from,#chat)",
            cacheManager = TYPE_REPOSITORY_CACHE_MANAGER,
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