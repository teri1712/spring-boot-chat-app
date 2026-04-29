package com.decade.practice.search.dto;

import java.util.UUID;

public record PeopleResponse(UUID id, String username, String name, String avatar) {
}
