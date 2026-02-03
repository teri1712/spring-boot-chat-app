package com.decade.practice.persistence.jpa.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@DiscriminatorValue("SEEN")
@Setter
@Getter
@NoArgsConstructor
public class SeenEvent extends ChatEvent {

    @Column(updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Instant at;

    public SeenEvent(Chat chat, User sender, Instant at) {
        super(chat, sender, "SEEN");
        this.at = at;
    }

    public SeenEvent(SeenEvent event) {
        this(event.getChat(), event.getSender(), event.getAt());
    }

    @Override
    public ChatEvent clone() {
        return new SeenEvent(this);
    }

}