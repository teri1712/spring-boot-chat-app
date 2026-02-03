package com.decade.practice.dto;

import com.decade.practice.persistence.jpa.embeddables.ChatIdentifier;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
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
