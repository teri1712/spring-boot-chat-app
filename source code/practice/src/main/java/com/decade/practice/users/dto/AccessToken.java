package com.decade.practice.users.dto;

public record AccessToken(
          String accessToken,
          String refreshToken
) {
}