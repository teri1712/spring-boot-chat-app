package com.decade.practice.dto;

public record ImageResponse(
        String uri,
        String filename,
        Integer width,
        Integer height,
        String format
) {
}
