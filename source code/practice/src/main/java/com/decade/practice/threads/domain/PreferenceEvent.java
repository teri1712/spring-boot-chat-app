package com.decade.practice.threads.domain;

import jakarta.annotation.Nullable;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.UUID;

@Getter
@Entity
@DiscriminatorValue("PREFERENCE")
public class PreferenceEvent extends MessageEvent {

    @NotNull
    private Integer iconId;

    @Nullable
    private String roomName;

    @Nullable
    private String roomAvatar;

    @Nullable
    private String theme;

    public PreferenceEvent() {
    }

    public PreferenceEvent(UUID senderId, UUID ownerId, String chatId, String roomNameSnapshot, String roomAvatarSnapshot, Integer iconId, String roomName, String roomAvatar, String theme) {
        super(senderId, "PREFERENCE", ownerId, chatId, roomNameSnapshot, roomAvatarSnapshot);
        this.iconId = iconId;
        this.roomName = roomName;
        this.roomAvatar = roomAvatar;
        this.theme = theme;
    }

    @Override
    public String getMessage() {
        return (isMine() ? "You has changed " : "Changed ") + "preference";
    }
}
