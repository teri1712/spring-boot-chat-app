package com.decade.practice.application.services;

import com.decade.practice.api.dto.EventDto;
import com.decade.practice.application.usecases.EventFactoryResolution;
import com.decade.practice.application.usecases.EventService;
import com.decade.practice.persistence.jpa.embeddables.ChatIdentifier;
import com.decade.practice.persistence.jpa.entities.ChatEvent;
import com.decade.practice.persistence.jpa.repositories.EventRepository;
import com.decade.practice.utils.EventUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.function.Function;

@Slf4j
@Service
@AllArgsConstructor
public class EventServiceImpl implements EventService {

    private EventRepository eventRepository;
    private EventFactoryResolution factoryResolution;

    @Override
    @PreAuthorize("@eventStore.isAllowed(#chatIdentifier,#userId)")
    public List<EventDto> findByOwnerAndChatAndEventVersionLessThanEqual(UUID userId, ChatIdentifier chatIdentifier, int eventVersion) {
        log.trace("Finding events for owner '{}' and chat '{}'", userId, chatIdentifier);
        return eventRepository.findByOwner_IdAndChat_IdentifierAndEventVersionLessThanEqual(userId, chatIdentifier, eventVersion, EventUtils.EVENT_VERSION_LESS_THAN_EQUAL)
                .stream().map(new Function<ChatEvent, EventDto>() {
                    @Override
                    public EventDto apply(ChatEvent chatEvent) {
                        return factoryResolution.mapToDto(chatEvent);
                    }
                }).toList();
    }

    @Override
    public List<EventDto> findByOwnerAndEventVersionLessThanEqual(UUID userId, int eventVersion) {
        log.trace("Finding events for owner '{}'", userId);
        return eventRepository.findByOwner_IdAndEventVersionLessThanEqual(userId, eventVersion, EventUtils.EVENT_VERSION_LESS_THAN_EQUAL)
                .stream().map(new Function<ChatEvent, EventDto>() {
                    @Override
                    public EventDto apply(ChatEvent chatEvent) {
                        return factoryResolution.mapToDto(chatEvent);
                    }
                }).toList()
                ;
    }

    @Override
    public EventDto findFirstByOwnerOrderByEventVersionDesc(UUID userId) {
        return eventRepository.findFirstByOwner_IdOrderByEventVersionDesc(userId).map(new Function<ChatEvent, EventDto>() {
            @Override
            public EventDto apply(ChatEvent chatEvent) {
                return factoryResolution.mapToDto(chatEvent);
            }
        }).orElse(null);
    }
}
