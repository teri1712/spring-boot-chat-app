package com.decade.practice.entities.domain.entity;

import jakarta.persistence.Entity;

@Entity
public abstract class MediaEvent extends ChatEvent {

      private String mediaUrl;

      protected MediaEvent(Chat chat, User sender, String eventType) {
            super(chat, sender, eventType);
      }

      protected MediaEvent() {
            super();
      }

      public String getMediaUrl() {
            return mediaUrl;
      }

      public void setMediaUrl(String mediaUrl) {
            this.mediaUrl = mediaUrl;
      }
}
