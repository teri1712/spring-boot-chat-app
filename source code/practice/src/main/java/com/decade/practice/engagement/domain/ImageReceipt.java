package com.decade.practice.engagement.domain;

import com.decade.practice.engagement.domain.events.ImageParticipantPlaced;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Entity
@DiscriminatorValue("IMAGE")
@Getter
public class ImageReceipt extends Receipt {
    private String uri;
    private Integer width;
    private Integer height;
    private String filename;
    private String format;

    public ImageReceipt(UUID idempotentKey, String chatId, UUID senderId, String uri, Integer width, Integer height, String filename, String format) {
        super(idempotentKey, chatId, senderId);
        this.uri = uri;
        this.width = width;
        this.height = height;
        this.filename = filename;
        this.format = format;
    }

    protected ImageReceipt() {
    }

    public void place() {
        registerEvent(new ImageParticipantPlaced(getSenderId(), getChatId(), getIdempotentKey(), Instant.now(), uri, width, height, filename, format));
    }

}
