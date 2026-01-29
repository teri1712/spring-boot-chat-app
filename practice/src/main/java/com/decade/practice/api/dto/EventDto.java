package com.decade.practice.api.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
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
    private ChatResponse chat;


}
