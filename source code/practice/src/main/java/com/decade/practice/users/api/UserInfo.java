package com.decade.practice.users.api;

import java.util.UUID;

public record UserInfo(String username, String name, String avatar, UUID id) {
}
