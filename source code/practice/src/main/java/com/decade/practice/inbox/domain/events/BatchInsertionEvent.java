package com.decade.practice.inbox.domain.events;

import org.springframework.modulith.events.Externalized;

@Externalized()
public record BatchSaving(Integer lower, Integer upper) {
}
