package com.decade.practice.threads.application.ports.out;

import com.decade.practice.threads.dto.EventResponse;

import java.util.List;
import java.util.UUID;

public interface EventService {

    List<EventResponse> findByOwnerAndChatAndEventVersionLessThanEqual(
            UUID owner,
            String chatId,
            int eventVersion
    );

    List<EventResponse> findByOwnerAndEventVersionLessThanEqual(
            UUID owner,
            int eventVersion
    );

}
