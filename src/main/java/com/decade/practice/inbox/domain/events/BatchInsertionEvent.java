package com.decade.practice.inbox.domain.events;

import org.springframework.modulith.events.Externalized;

@Externalized("batch-insertion-placed::#{lower}-#{upper}")
public record BatchInsertionEvent(Integer lower, Integer upper, MessageCreated insertion) {
}
