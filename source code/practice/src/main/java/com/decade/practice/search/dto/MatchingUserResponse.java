package com.decade.practice.search.dto;

import java.util.UUID;

public record MatchingUserResponse(UUID id, String username, String name, String avatar) {
}
