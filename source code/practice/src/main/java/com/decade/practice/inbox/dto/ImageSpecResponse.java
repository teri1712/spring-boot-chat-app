package com.decade.practice.inbox.dto;

public record ImageSpecResponse(
          String uri,
          String filename,
          Integer width,
          Integer height,
          String format
) {
}
