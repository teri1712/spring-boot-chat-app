package com.decade.practice.inbox.domain;

import com.decade.practice.inbox.domain.events.ImageRoomEventCreated;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;

import java.util.UUID;

@Entity
@DiscriminatorValue("IMAGE")
@Getter
public class ImageRoomEvent extends RoomEvent {
      private String uri;
      private Integer width;
      private Integer height;
      private String filename;
      private String format;

      public ImageRoomEvent(UUID idempotentKey, String chatId, UUID senderId, String uri, Integer width, Integer height, String filename, String format) {
            super(idempotentKey, chatId, senderId);
            this.uri = uri;
            this.width = width;
            this.height = height;
            this.filename = filename;
            this.format = format;
      }

      protected ImageRoomEvent() {
      }


      @Override
      protected void onCreated() {
            registerEvent(ImageRoomEventCreated.builder()
                      .chatEventId(getPostingId())
                      .chatId(getChatId())

                      .senderId(getSenderId())
                      .uri(getUri())
                      .width(getWidth())
                      .height(getHeight())
                      .filename(getFilename())
                      .format(getFormat())
                      .build());
      }

}
