package com.decade.practice.entities.domain.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("FILE")
public class FileEvent extends MediaEvent {

      public FileEvent(Chat chat, User sender, String url) {
            super(chat, sender, "FILE");
            setMediaUrl(url);
      }

      protected FileEvent() {
      }

      @Override
      public ChatEvent copy() {
            return new FileEvent(getChat(), getSender(), getMediaUrl());
      }
}
