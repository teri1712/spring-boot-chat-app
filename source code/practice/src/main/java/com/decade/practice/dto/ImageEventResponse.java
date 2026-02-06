package com.decade.practice.dto;

import java.io.Serializable;

public record ImageEventResponse(
        String downloadUrl,
        String filename,
        Integer width,
        Integer height,
        String format
) implements Serializable {
}
