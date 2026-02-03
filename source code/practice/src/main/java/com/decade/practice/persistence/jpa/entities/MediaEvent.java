package com.decade.practice.persistence.jpa.entities;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public abstract class MediaEvent extends MessageEvent {

    private String mediaUrl;
    private int size;

    protected MediaEvent(Chat chat, User sender, String eventType) {
        super(chat, sender, eventType);
    }

    protected MediaEvent() {
        super();
    }


}
