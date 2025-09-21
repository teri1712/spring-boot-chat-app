package com.decade.practice.domain.entities;

import jakarta.persistence.Entity;

@Entity
public abstract class MessageEvent extends ChatEvent {
        protected MessageEvent(Chat chat, User sender, String eventType) {
                super(chat, sender, eventType);
        }

        protected MessageEvent() {

        }
}
