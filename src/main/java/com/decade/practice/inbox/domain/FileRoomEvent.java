package com.decade.practice.inbox.domain;

import com.decade.practice.inbox.domain.events.FileRoomEventCreated;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;

import java.util.UUID;

@Entity
@DiscriminatorValue("FILE")
@Getter
public class FileRoomEvent extends RoomEvent {
      private String uri;
      private String filename;
      private Integer size;

      protected FileRoomEvent() {
      }

      public FileRoomEvent(UUID idempotentKey, String chatId, UUID senderId, String uri, String filename, Integer size) {
            super(idempotentKey, chatId, senderId);
            this.uri = uri;
            this.filename = filename;
            this.size = size;
      }

      @Override
      protected void onCreated() {
            registerEvent(FileRoomEventCreated.builder()
                      .chatEventId(getPostingId())
                      .chatId(getChatId())


                      .senderId(getSenderId())
                      .uri(getUri())
                      .filename(getFilename())
                      .size(getSize())
                      .build());
      }

}
