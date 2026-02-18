package com.decade.practice.threads.domain.events;

import java.util.UUID;

public record ThreadIncremented(
        UUID eventId,
        Integer eventVersion) {


}
