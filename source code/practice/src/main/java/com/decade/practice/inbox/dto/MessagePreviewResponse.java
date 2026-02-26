package com.decade.practice.inbox.dto;


import java.time.Instant;

public record MessagePreviewResponse(PartnerResponse sentBy, String content, Instant createdAt) {
}
