package com.decade.practice.threads.application.services;

import com.decade.practice.threads.application.ports.out.EventService;
import com.decade.practice.threads.dto.EventResponse;
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
public class CacheableEventService implements EventService {

    public final EventService eventService;

    @Override
    @Cacheable(cacheNames = "events", key = "#owner + ':' + #chat + ':' + #eventVersion")
    public List<EventResponse> findByOwnerAndChatAndEventVersionLessThanEqual(UUID owner, String chatId, int eventVersion) {
        log.trace("Events for identifier: {} and ownerId : {} are about to be cached", chatId, owner);
        return eventService.findByOwnerAndChatAndEventVersionLessThanEqual(owner, chatId, eventVersion);
    }

    @Override
    @Cacheable(cacheNames = "events", key = "#owner + ':' + #eventVersion")
    public List<EventResponse> findByOwnerAndEventVersionLessThanEqual(UUID owner, int eventVersion) {
        log.trace("Events for ownerId: {} are about to be cached", owner);
        return eventService.findByOwnerAndEventVersionLessThanEqual(owner, eventVersion);
    }

}
