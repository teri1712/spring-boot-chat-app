package com.decade.practice.dto;

public record ImageEventResponse(
        String downloadUrl,
        String filename,
        Integer width,
        Integer height,
        String format
) {
}
