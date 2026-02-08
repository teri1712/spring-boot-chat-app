package com.decade.practice.application.domain;

import java.util.UUID;

public interface AccessPolicy {
    boolean isAllowed(String chatId, UUID userId);
}
