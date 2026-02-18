package com.decade.practice.threads.dto;

public record ImageResponse(
        String uri,
        String filename,
        Integer width,
        Integer height,
        String format
) {
}
