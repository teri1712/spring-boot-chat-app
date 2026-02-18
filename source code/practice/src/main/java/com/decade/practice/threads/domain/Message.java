package com.decade.practice.threads.domain;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record Message(UUID sendBy, String content, Instant createdAt) {
    public HashValue computeHash() {
        HashValue hashValue = new HashValue(createdAt.toEpochMilli());
        for (int i = 0; i < content.length(); i++) {
            char c = content.charAt(i);
            hashValue = hashValue.plus(new HashValue((long) c));
        }
        return hashValue;
    }

    public static HashValue computeHash(List<Message> messages) {
        HashValue hashValue = new HashValue(0L);
        for (int i = 0; i < messages.size(); i++) {
            hashValue = hashValue.plus(messages.get(i).computeHash());
        }
        return hashValue;
    }
}
