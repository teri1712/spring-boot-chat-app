package com.decade.practice.search.dto;

import java.util.UUID;

public record MatchingUserResponse(UUID id, String name, String avatar) {
}
