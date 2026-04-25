package com.decade.practice.search.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Table(name = "message_history")
public record MessageDocument(
    @Id
    UUID id,
    String content,
    @Column("sequence_number")
    Long sequenceNumber,
    @Column("chat_id")
    String chatId,
    @Column("created_at")
    Instant createdAt
) {
}
