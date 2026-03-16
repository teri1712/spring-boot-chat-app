package com.decade.practice.inbox.domain;

import com.decade.practice.inbox.domain.events.TextRoomEventCreated;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;

import java.util.UUID;

@Entity
@DiscriminatorValue("TEXT")
@Getter
public class TextRoomEvent extends RoomEvent {

      private String content;

      public TextRoomEvent(UUID postingId, String chatId, UUID senderId, String content) {
            super(postingId, chatId, senderId);
            this.content = content;
      }

      protected TextRoomEvent() {
      }

      @Override
      protected void onCreated() {
            registerEvent(TextRoomEventCreated.builder()
                      .chatEventId(getPostingId())
                      .chatId(getChatId())

                      .senderId(getSenderId())
                      .content(content)
                      .build());
      }


}
