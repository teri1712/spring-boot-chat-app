package com.decade.practice.application.usecases;

import com.decade.practice.api.dto.EventDto;
import com.decade.practice.persistence.jpa.embeddables.ChatIdentifier;

import java.util.List;
import java.util.UUID;

public interface EventService {

    List<EventDto> findByOwnerAndChatAndEventVersionLessThanEqual(
            UUID owner,
            ChatIdentifier chat,
            int eventVersion
    );

    List<EventDto> findByOwnerAndEventVersionLessThanEqual(
            UUID owner,
            int eventVersion
    );

    EventDto findFirstByOwnerOrderByEventVersionDesc(
            UUID owner
    );
}
