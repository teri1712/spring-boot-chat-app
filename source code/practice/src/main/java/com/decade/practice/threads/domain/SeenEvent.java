package com.decade.practice.threads.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@DiscriminatorValue("SEEN")
@Setter
@Getter
@NoArgsConstructor
public class SeenEvent extends ChatEvent {

    @Column(updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Instant at;

    public SeenEvent(UUID senderId, UUID ownerId, String chatId, String roomNameSnapshot, String roomAvatarSnapshot, Instant at) {
        super(senderId, "SEEN", ownerId, chatId, roomNameSnapshot, roomAvatarSnapshot);
        this.at = at;
    }
}