package com.decade.practice.dto;

public record FileEventResponse(
        String filename,
        int size,
        String mediaUrl
) {
}
