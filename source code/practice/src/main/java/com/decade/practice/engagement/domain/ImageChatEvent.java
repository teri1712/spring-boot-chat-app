package com.decade.practice.engagement.domain;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;

import java.util.UUID;

@Entity
@DiscriminatorValue("IMAGE")
@Getter
public class ImageChatEvent extends ChatEvent {
      private String uri;
      private Integer width;
      private Integer height;
      private String filename;
      private String format;

      public ImageChatEvent(UUID idempotentKey, String chatId, UUID senderId, String uri, Integer width, Integer height, String filename, String format) {
            super(idempotentKey, chatId, senderId);
            this.uri = uri;
            this.width = width;
            this.height = height;
            this.filename = filename;
            this.format = format;
      }

      protected ImageChatEvent() {
      }

}
