package com.decade.practice.api.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
public class EventDto {

    private UUID id;
    private UUID sender;

    private TextEventDto textEvent;
    private ImageEventDto imageEvent;
    private IconEventDto iconEvent;
    private PreferenceEventDto preferenceEvent;
    private FileEventDto fileEvent;
    private SeenEventDto seenEvent;

    private String eventType;
    private UserResponse owner;
    private UserResponse partner;
    private ChatResponse chat;


}
