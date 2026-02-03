package com.decade.practice.application.domain;

import com.decade.practice.persistence.jpa.embeddables.ChatIdentifier;

import java.util.UUID;

public interface AccessPolicy {
    boolean isAllowed(ChatIdentifier chatIdentifier, UUID userId);
}
