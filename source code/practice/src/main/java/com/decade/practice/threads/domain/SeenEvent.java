package com.decade.practice.threads.domain;

import com.decade.practice.threads.domain.events.SeenReady;
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

    public SeenEvent(UUID senderId, UUID ownerId, String chatId, Instant at) {
        super(senderId, "SEEN", ownerId, chatId);
        this.at = at;
    }


    @Override
    public void setEventVersion(Integer eventVersion) {
        super.setEventVersion(eventVersion);
        registerEvent(new SeenReady(getSenderId(), getChatId(), getOwnerId()));
    }
}