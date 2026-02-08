package com.decade.practice.dto;

public record ChatDetails(
        ChatResponse chat,
        UserResponse partner,
        PreferenceResponse preference
) {
}
