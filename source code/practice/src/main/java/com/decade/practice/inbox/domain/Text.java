package com.decade.practice.inbox.domain;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

import static com.decade.practice.inbox.domain.Text.TEXT_TYPE;

@Entity
@DiscriminatorValue(TEXT_TYPE)
@Setter
@Getter
@NoArgsConstructor
public class Text extends Message {

      public static final String TEXT_TYPE = "TEXT";

      @Column(updatable = false)
      private String content;

      public Text(UUID chatEventId, UUID senderId, String chatId, Instant createdAt, String content) {
            super(chatEventId, senderId, chatId, createdAt, TEXT_TYPE);
            this.content = content;
      }

      @Override
      public MessageState getState() {
            return TextState.
                      builder()
                      .sequenceId(getSequenceId())
                      .chatEventId(getChatEventId())
                      .senderId(getSenderId())
                      .messageType(getMessageType())
                      .chatId(getChatId())
                      .createdAt(getCreatedAt())
                      .seenByIds(getSeenPointers().keySet())
                      .content(getContent())
                      .build();
      }

}
