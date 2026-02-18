package com.decade.practice.threads.domain;

import com.decade.practice.threads.domain.events.PreferenceReady;
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

    public PreferenceEvent(UUID senderId, UUID ownerId, String chatId, Integer iconId, String roomName, String roomAvatar, String theme) {
        super(senderId, "PREFERENCE", ownerId, chatId);
        this.iconId = iconId;
        this.roomName = roomName;
        this.roomAvatar = roomAvatar;
        this.theme = theme;
    }

    @Override
    public String getMessage() {
        return (isMine() ? "You has changed " : "Changed ") + "preference";
    }

    @Override
    public void setEventVersion(Integer eventVersion) {
        super.setEventVersion(eventVersion);
        registerEvent(new PreferenceReady(getChatId(), getOwnerId(), getRoomName(), getRoomAvatar()));
    }
}
