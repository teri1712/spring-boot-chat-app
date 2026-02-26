package com.decade.practice.inbox.dto;

import java.util.UUID;

public record PartnerResponse(UUID id, String username, String name, String avatar) {
}
