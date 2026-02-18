package com.decade.practice.engagement.domain;

import com.decade.practice.engagement.domain.events.PreferenceParticipantPlaced;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Entity
@DiscriminatorValue("Preference")
@Getter
public class PreferenceReceipt extends Receipt {

    private Integer iconId;
    private String roomName;
    private String roomAvatar;
    private Long themeId;

    public PreferenceReceipt(UUID idempotentKey, String chatId, UUID senderId, Integer iconId, String roomName, String roomAvatar, Long themeId) {
        super(idempotentKey, chatId, senderId);
        this.iconId = iconId;
        this.roomName = roomName;
        this.roomAvatar = roomAvatar;
        this.themeId = themeId;
    }

    protected PreferenceReceipt() {
    }

    public void place() {
        registerEvent(new PreferenceParticipantPlaced(getSenderId(), getChatId(), getIdempotentKey(), Instant.now(), iconId, roomName, roomAvatar, themeId));
    }

}
