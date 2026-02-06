package com.decade.practice.dto;

import java.io.Serializable;

public record FileEventResponse(
        String filename,
        int size,
        String mediaUrl
) implements Serializable {
}
