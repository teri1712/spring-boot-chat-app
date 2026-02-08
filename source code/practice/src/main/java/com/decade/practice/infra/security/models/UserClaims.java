package com.decade.practice.infra.security.models;

import com.decade.practice.dto.ImageResponse;

import java.util.UUID;

public record UserClaims(
        UUID id,
        String username,
        String name,
        String role,
        Float gender,
        ImageResponse avatar

) {

}