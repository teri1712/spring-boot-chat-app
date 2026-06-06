package com.decade.practice.inbox.domain;

import jakarta.persistence.Embeddable;

@Embeddable
public record RoomInfo(String customName, String customAvatar) {
}
