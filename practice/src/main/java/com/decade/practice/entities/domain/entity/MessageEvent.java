package com.decade.practice.entities.domain.entity;

import jakarta.persistence.Entity;

@Entity
public abstract class MessageEvent extends ChatEvent {
      protected MessageEvent(Chat chat, User sender, String eventType) {
            super(chat, sender, eventType);
      }

      protected MessageEvent() {

      }
}
