package com.decade.practice.inbox.application.services;

import com.decade.practice.inbox.application.query.LogService;
import com.decade.practice.inbox.dto.InboxLogResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@Primary
@AllArgsConstructor
@ConditionalOnProperty(name = "server.cache.events", havingValue = "true", matchIfMissing = true)
public class CacheableLogService implements LogService {

      public final LogService logService;

      @Override
      @Cacheable(cacheNames = "events", key = "#owner + ':' + #chatId + ':' + #sequenceId")
      public List<InboxLogResponse> findByChatAndSequenceLessThanEqual(UUID owner, String chatId, Long sequenceId) {
            log.trace("Events for identifier: {} and ownerId : {} are about to be cached", chatId, owner);
            return logService.findByChatAndSequenceLessThanEqual(owner, chatId, sequenceId);
      }

      @Override
      @Cacheable(cacheNames = "events", key = "#owner + ':' + #sequenceId")
      public List<InboxLogResponse> findBySequenceLessThanEqual(UUID owner, Long sequenceId) {
            log.trace("Events for ownerId: {} are about to be cached", owner);
            return logService.findBySequenceLessThanEqual(owner, sequenceId);
      }

}
