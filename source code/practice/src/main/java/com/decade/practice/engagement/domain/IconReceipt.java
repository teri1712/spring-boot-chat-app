package com.decade.practice.engagement.domain;

import com.decade.practice.engagement.domain.events.IconParticipantPlaced;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Entity
@DiscriminatorValue("ICON")
@Getter
public class IconReceipt extends Receipt {

    private Integer iconId;

    protected IconReceipt() {
    }

    public IconReceipt(UUID idempotentKey, String chatId, UUID senderId, Integer iconId) {
        super(idempotentKey, chatId, senderId);
        this.iconId = iconId;
    }

    public void place() {
        registerEvent(new IconParticipantPlaced(getSenderId(), getChatId(), getIdempotentKey(), Instant.now(), iconId));
    }

}
