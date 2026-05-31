package com.decade.practice.inbox.domain.events;

import org.springframework.modulith.events.Externalized;

@Externalized("batch-update-placed::#{lower}-#{upper}")
public record BatchUpdateEvent(Integer lower, Integer upper, MessageUpdated update) {
}
