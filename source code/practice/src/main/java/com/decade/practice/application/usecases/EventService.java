package com.decade.practice.application.usecases;

import com.decade.practice.dto.EventDetails;
import com.decade.practice.dto.EventResponse;
import com.decade.practice.persistence.jpa.embeddables.ChatIdentifier;

import java.util.List;
import java.util.UUID;

public interface EventService {

    List<EventResponse> findByOwnerAndChatAndEventVersionLessThanEqual(
            UUID owner,
            ChatIdentifier chat,
            int eventVersion
    );

    List<EventResponse> findByOwnerAndEventVersionLessThanEqual(
            UUID owner,
            int eventVersion
    );

    EventDetails findFirstByOwnerOrderByEventVersionDesc(
            UUID owner
    );
}
