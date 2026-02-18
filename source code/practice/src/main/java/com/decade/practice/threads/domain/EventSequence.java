package com.decade.practice.threads.domain;

import jakarta.persistence.Embeddable;
import lombok.Getter;

import java.util.UUID;

@Getter
@Embeddable
class EventSequence {
    public static final int STARTING_VERSION = 0;
    private Integer eventVersion = STARTING_VERSION;
    private UUID eventId;

    public Integer increment(UUID eventId) {
        this.eventId = eventId;
        return ++this.eventVersion;
    }
}