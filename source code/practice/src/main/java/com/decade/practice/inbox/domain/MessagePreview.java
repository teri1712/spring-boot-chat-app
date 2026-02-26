package com.decade.practice.inbox.domain;

import java.time.Instant;
import java.util.UUID;

public record MessagePreview(Long id, UUID sentBy, String content, Instant createdAt) {

}
