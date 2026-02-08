package com.decade.practice.dto;

import java.util.Date;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String username,
        String name,
        Date dob,
        String role,
        ImageResponse avatar,
        Float gender
) {
}
