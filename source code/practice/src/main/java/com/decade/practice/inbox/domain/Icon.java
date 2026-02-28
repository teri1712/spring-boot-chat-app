package com.decade.practice.inbox.domain;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

import static com.decade.practice.inbox.domain.Icon.ICON_TYPE;

@Entity
@DiscriminatorValue(ICON_TYPE)
@Setter
@Getter
@NoArgsConstructor
public class Icon extends Message {

      @Column(updatable = false)
      private Integer iconId;

      public Icon(UUID chatEventId, UUID senderId, String chatId, Instant createdAt, Integer iconId) {
            super(chatEventId, senderId, chatId, createdAt, ICON_TYPE);
            this.iconId = iconId;
      }


      public static final String ICON_TYPE = "ICON";

      @Override
      public MessageState getState() {
            return IconState.builder()
                      .sequenceId(getSequenceId())
                      .chatEventId(getChatEventId())
                      .senderId(getSenderId())
                      .messageType(getMessageType())
                      .chatId(getChatId())
                      .createdAt(getCreatedAt())
                      .seenByIds(getSeenPointers().keySet())
                      .iconId(getIconId())
                      .build();
      }
}