package com.decade.practice.users.api;

import java.util.UUID;

// TODO: Cache
public record UserInfo(String username, String name, String avatar, UUID id) {
}
