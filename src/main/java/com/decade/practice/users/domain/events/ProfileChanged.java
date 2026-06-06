package com.decade.practice.users.domain.events;

import java.time.Instant;
import java.util.UUID;

public record ProfileChanged(
    UUID userId,
    String username,
    String name,
    String gender,
    Instant dob,
    String avatar
) {
}
