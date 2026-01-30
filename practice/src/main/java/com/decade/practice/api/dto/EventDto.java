package com.decade.practice.api.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
public class EventDto {

    private UUID id;
    private UUID idempotencyKey;
    private UUID sender;

    private TextEventDto textEvent;
    private ImageEventDto imageEvent;
    private IconEventDto iconEvent;
    private PreferenceEventDto preferenceEvent;
    private FileEventDto fileEvent;
    private SeenEventDto seenEvent;

    private Instant createdTime;
    private String eventType;
    private Integer eventVersion;
    private boolean message;

    private UserResponse owner;
    private UserResponse partner;
    private ChatDto chat;


}
