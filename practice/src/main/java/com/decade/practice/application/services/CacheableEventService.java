package com.decade.practice.application.services;

import com.decade.practice.api.dto.EventDto;
import com.decade.practice.application.usecases.EventService;
import com.decade.practice.application.usecases.EventStore;
import com.decade.practice.persistence.jpa.embeddables.ChatIdentifier;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

// TODO: Test effect
@Slf4j
@Service
@Primary
@AllArgsConstructor
@ConditionalOnProperty(name = "server.cache.events", havingValue = "true", matchIfMissing = true)
public class CacheableEventService implements EventService {

    public final EventStore eventService;

    @Override
    @Cacheable(cacheNames = "events", key = "#owner + ':' + #chat + ':' + #eventVersion")
    public List<EventDto> findByOwnerAndChatAndEventVersionLessThanEqual(UUID owner, ChatIdentifier chat, int eventVersion) {
        log.trace("Events for chatId: {} and owner : {} are about to be cached", chat, owner);
        return eventService.findByOwnerAndChatAndEventVersionLessThanEqual(owner, chat, eventVersion);
    }

    @Override
    @Cacheable(cacheNames = "events", key = "#owner + ':' + #eventVersion")
    public List<EventDto> findByOwnerAndEventVersionLessThanEqual(UUID owner, int eventVersion) {
        log.trace("Events for owner: {} are about to be cached", owner);
        return eventService.findByOwnerAndEventVersionLessThanEqual(owner, eventVersion);
    }

    @Override
    public EventDto findFirstByOwnerOrderByEventVersionDesc(UUID owner) {
        return eventService.findFirstByOwnerOrderByEventVersionDesc(owner);
    }

}
