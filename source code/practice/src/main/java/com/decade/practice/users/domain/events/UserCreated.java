package com.decade.practice.users.domain.events;

import java.util.Date;
import java.util.UUID;

public record UserCreated(
        UUID userId,
        String username,
        String name,
        Float gender,
        Date dob,
        String avatar
) {
}
