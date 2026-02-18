package com.decade.practice.threads.domain;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@DiscriminatorValue("ICON")
@Setter
@Getter
@NoArgsConstructor
public class IconEvent extends MessageEvent {

    @Column(updatable = false)
    private int iconId;

    public IconEvent(UUID senderId, UUID ownerId, String chatId, String roomNameSnapshot, String roomAvatarSnapshot, int iconId) {
        super(senderId, "ICON", ownerId, chatId, roomNameSnapshot, roomAvatarSnapshot);
        this.iconId = iconId;
    }

    @Override
    public String getMessage() {
        return (isMine() ? "You has sent " : "Sent ") + " an icon";
    }
}