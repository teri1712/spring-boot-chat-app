package com.decade.practice.users.api.events;

import java.util.Date;
import java.util.UUID;

//@Externalized("users.user.created::#{#this.userId}")
public record IntegrationUserCreated(
        UUID userId,
        String username,
        String name,
        String gender,
        Date dob,
        String avatar
) {
}
