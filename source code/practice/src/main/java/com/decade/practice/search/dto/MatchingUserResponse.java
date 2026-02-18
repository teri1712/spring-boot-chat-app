package com.decade.practice.search.dto;

import java.util.UUID;

public record MatchingUserResponse(UUID userId, String name, String avatar) {
}
