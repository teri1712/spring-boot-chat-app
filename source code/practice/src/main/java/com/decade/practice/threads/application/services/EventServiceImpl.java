package com.decade.practice.threads.application.services;

import com.decade.practice.engagement.api.EngagementFacade;
import com.decade.practice.engagement.api.EngagementRule;
import com.decade.practice.threads.application.ports.out.EventRepository;
import com.decade.practice.threads.application.ports.out.EventService;
import com.decade.practice.threads.domain.ChatEventPolicy;
import com.decade.practice.threads.dto.EventResponse;
import com.decade.practice.threads.dto.mapper.EventMapper;
import com.decade.practice.threads.utils.EventUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository events;
    private final EventMapper eventMapper;
    private final EngagementFacade engagementFacade;
    private final ChatEventPolicy chatEventPolicy;

    @Override
    public List<EventResponse> findByOwnerAndChatAndEventVersionLessThanEqual(UUID userId, String chatId, int eventVersion) {
        EngagementRule engagementRule = engagementFacade.find(chatId, userId);
        chatEventPolicy.applyRead(engagementRule);
        log.trace("Finding events for ownerId '{}' and chat '{}'", userId, chatId);
        return events.findByOwnerIdAndChatIdAndEventVersionLessThanEqual(userId, chatId, eventVersion, EventUtils.EVENT_VERSION_LESS_THAN_EQUAL)
                .stream().map(eventMapper::toDto).toList();
    }

    @Override
    public List<EventResponse> findByOwnerAndEventVersionLessThanEqual(UUID userId, int eventVersion) {
        log.trace("Finding events for ownerId '{}'", userId);
        return events.findByOwnerIdAndEventVersionLessThanEqual(userId, eventVersion, EventUtils.EVENT_VERSION_LESS_THAN_EQUAL)
                .stream().map(eventMapper::toDto).toList();
    }
}
