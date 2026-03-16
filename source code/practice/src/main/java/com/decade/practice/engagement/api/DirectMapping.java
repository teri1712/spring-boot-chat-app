package com.decade.practice.engagement.api;

import java.util.UUID;

public record DirectMapping(UUID caller, UUID partner, String chatId) {
}
