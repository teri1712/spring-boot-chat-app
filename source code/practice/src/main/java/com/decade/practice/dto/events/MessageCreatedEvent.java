package com.decade.practice.dto.events;

import com.decade.practice.dto.ChatDto;
import com.decade.practice.dto.EventDto;
import com.decade.practice.dto.TextEventDto;
import com.decade.practice.dto.UserResponse;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessageCreatedEvent {

    private UUID idempotencyKey;
    private UUID sender;
    private ChatDto chat;

    private UserResponse partner;
    private TextEventDto textEvent;


    public static MessageCreatedEvent from(EventDto eventDto) {
        if (eventDto.getTextEvent() == null)
            return null;
        return MessageCreatedEvent.builder()
                .idempotencyKey(eventDto.getIdempotencyKey())
                .sender(eventDto.getSender())
                .chat(eventDto.getChat())
                .partner(eventDto.getPartner())
                .textEvent(eventDto.getTextEvent())
                .build();
    }
}
