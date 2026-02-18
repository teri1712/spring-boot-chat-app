package com.decade.practice.threads.domain;

import com.decade.practice.threads.domain.events.MessageReady;
import jakarta.persistence.Entity;
import lombok.Getter;

import java.util.UUID;

@Entity
@Getter
public abstract class MessageEvent extends ChatEvent {

    protected MessageEvent() {
    }

    public MessageEvent(UUID senderId, String eventType, UUID ownerId, String chatId, String roomNameSnapshot, String roomAvatarSnapshot) {
        super(senderId, eventType, ownerId, chatId, roomNameSnapshot, roomAvatarSnapshot);
    }

    public abstract String getMessage();

    @Override
    public void setEventVersion(Integer eventVersion) {
        super.setEventVersion(eventVersion);
        registerEvent(new MessageReady(getId(), getMessage(), getCreatedAt(), getOwnerId(), getChatId(), getSenderId()));
    }
}
