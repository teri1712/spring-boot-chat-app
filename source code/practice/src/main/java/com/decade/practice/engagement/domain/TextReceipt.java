package com.decade.practice.engagement.domain;

import com.decade.practice.engagement.domain.events.TextParticipantPlaced;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Entity
@DiscriminatorValue("TEXT")
@Getter
public class TextReceipt extends Receipt {

    private String content;

    public TextReceipt(UUID idempotentKey, String chatId, UUID senderId, String content) {
        super(idempotentKey, chatId, senderId);
        this.content = content;
    }

    protected TextReceipt() {
    }


    @Override
    public void place() {
        registerEvent(new TextParticipantPlaced(getSenderId(), getChatId(), getIdempotentKey(), Instant.now(), content));
    }

}
