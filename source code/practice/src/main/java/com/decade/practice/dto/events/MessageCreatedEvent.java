package com.decade.practice.dto.events;

import com.decade.practice.dto.*;

import java.util.UUID;

// Event-Carried State Transfer
public record MessageCreatedEvent(
        UUID idempotencyKey,
        UUID sender,
        ChatResponse chat,
        UserResponse partner,
        TextEventResponse textEvent
) {

    public static MessageCreatedEvent from(EventDetails eventDetails) {
        EventResponse eventResponse = eventDetails.event();
        Conversation conversation = eventDetails.conversation();
        if (eventResponse.textEvent() == null)
            return null;
        return new MessageCreatedEvent(
                eventResponse.idempotencyKey(),
                eventResponse.sender(),
                eventResponse.chat(),
                conversation.partner(),
                eventResponse.textEvent()
        );
    }
}
