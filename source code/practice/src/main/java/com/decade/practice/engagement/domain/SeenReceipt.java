package com.decade.practice.engagement.domain;

import com.decade.practice.engagement.domain.events.SeenParticipantPlaced;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Entity
@DiscriminatorValue("SEEN")
@Getter
public class SeenReceipt extends Receipt {

    private Instant at;

    public SeenReceipt(UUID idempotentKey, String chatId, UUID senderId, Instant at) {
        super(idempotentKey, chatId, senderId);
        this.at = at;
    }

    protected SeenReceipt() {
    }


    public void place() {
        registerEvent(new SeenParticipantPlaced(getSenderId(), getChatId(), getIdempotentKey(), Instant.now(), at));
    }


}
