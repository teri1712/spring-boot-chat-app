package com.decade.practice.users.dto;

import java.util.Date;
import java.util.UUID;

public record ProfileResponse(
        UUID id,
        String username,
        String name,
        Date dob,
        String role,
        String avatar,
        Float gender
) {
}
