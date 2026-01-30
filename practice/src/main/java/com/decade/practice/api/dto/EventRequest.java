package com.decade.practice.api.dto;

import com.decade.practice.persistence.jpa.embeddables.ChatIdentifier;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

// TODO: re-implement client
@Data
@NoArgsConstructor
public class EventRequest {

    private TextEventDto textEvent;
    private ImageEventDto imageEvent;
    private IconEventDto iconEvent;
    private PreferenceRequest preferenceEvent;
    private FileEventDto fileEvent;
    private SeenEventDto seenEvent;

    private UUID sender;

    // TODO: check client
    private ChatIdentifier chatIdentifier;

}
