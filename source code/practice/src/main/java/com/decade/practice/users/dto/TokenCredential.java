package com.decade.practice.users.dto;

public record TokenCredential(
        String accessToken,
        String refreshToken
) {
}