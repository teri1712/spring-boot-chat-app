package com.decade.practice.application.services;

import com.decade.practice.application.usecases.EventService;
import com.decade.practice.application.usecases.EventStore;
import com.decade.practice.dto.EventDetails;
import com.decade.practice.dto.EventResponse;
import com.decade.practice.persistence.jpa.embeddables.ChatIdentifier;
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

    public final EventStore eventStore;

    @Override
    @Cacheable(cacheNames = "events", key = "#owner + ':' + #chat + ':' + #eventVersion")
    public List<EventResponse> findByOwnerAndChatAndEventVersionLessThanEqual(UUID owner, ChatIdentifier chat, int eventVersion) {
        log.trace("Events for chatId: {} and owner : {} are about to be cached", chat, owner);
        return eventStore.findByOwnerAndChatAndEventVersionLessThanEqual(owner, chat, eventVersion);
    }

    @Override
    @Cacheable(cacheNames = "events", key = "#owner + ':' + #eventVersion")
    public List<EventResponse> findByOwnerAndEventVersionLessThanEqual(UUID owner, int eventVersion) {
        log.trace("Events for owner: {} are about to be cached", owner);
        return eventStore.findByOwnerAndEventVersionLessThanEqual(owner, eventVersion);
    }

    @Override
    public EventDetails findFirstByOwnerOrderByEventVersionDesc(UUID owner) {
        return eventStore.findFirstByOwnerOrderByEventVersionDesc(owner);
    }

}
