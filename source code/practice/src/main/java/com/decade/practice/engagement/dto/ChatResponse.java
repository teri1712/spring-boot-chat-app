package com.decade.practice.engagement.dto;

// TODO: Adjust client
public record ChatResponse(
        String identifier,
        Boolean freshOne,
        PreferenceResponse preference) {
}
