package com.decade.practice.inbox.domain;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Getter
@Entity
@DiscriminatorValue("GROUP")
@NoArgsConstructor
public class HelloGroup extends Message {

      private UUID creator;

      public HelloGroup(UUID postingId, UUID creator, String chatId, Instant createdAt) {
            super(postingId, creator, chatId, createdAt, "GROUP");
            this.creator = creator;
      }

      @Override
      public MessageState getState() {
            return HelloGroupState.builder()
                      .sequenceId(getSequenceId())
                      .postingId(getPostingId())
                      .senderId(getSenderId())
                      .messageType(getMessageType())
                      .chatId(getChatId())
                      .createdAt(getCreatedAt())
                      .seenByIds(getAllSeenPointers().keySet())
                      .creator(getCreator())
                      .build();
      }
}
