package com.decade.practice.threads.domain;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@DiscriminatorValue("TEXT")
@Setter
@Getter
@NoArgsConstructor
public class TextEvent extends MessageEvent {

    @Column(updatable = false)
    private String content;

    public TextEvent(UUID senderId, UUID ownerId, String chatId, String roomNameSnapshot, String roomAvatarSnapshot, String content) {
        super(senderId, "TEXT", ownerId, chatId, roomNameSnapshot, roomAvatarSnapshot);
        this.content = content;
    }

    @Override
    public String getMessage() {
        return (isMine() ? "You: " : "") + getContent();
    }
}
