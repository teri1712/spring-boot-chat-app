package com.decade.practice.dto;

public record Conversation(
        ChatResponse chat,
        UserResponse partner,
        UserResponse owner

) {
}