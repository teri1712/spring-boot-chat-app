package com.decade.practice.engagement.domain;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;

import java.util.UUID;

@Entity
@DiscriminatorValue("FILE")
@Getter
public class FileChatEvent extends ChatEvent {
      private String uri;
      private String filename;
      private Integer size;

      protected FileChatEvent() {
      }

      public FileChatEvent(UUID idempotentKey, String chatId, UUID senderId, String uri, String filename, Integer size) {
            super(idempotentKey, chatId, senderId);
            this.uri = uri;
            this.filename = filename;
            this.size = size;
      }


}
