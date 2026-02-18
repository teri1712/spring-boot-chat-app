package com.decade.practice.engagement.domain;

import com.decade.practice.engagement.domain.events.FileParticipantPlaced;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Entity
@DiscriminatorValue("FILE")
@Getter
public class FileReceipt extends Receipt {
    private String uri;
    private String filename;
    private Integer size;

    protected FileReceipt() {
    }

    public FileReceipt(UUID idempotentKey, String chatId, UUID senderId, String uri, String filename, Integer size) {
        super(idempotentKey, chatId, senderId);
        this.uri = uri;
        this.filename = filename;
        this.size = size;
    }

    public void place() {
        registerEvent(new FileParticipantPlaced(getSenderId(), getChatId(), getIdempotentKey(), Instant.now(), uri, filename, size));
    }

}
