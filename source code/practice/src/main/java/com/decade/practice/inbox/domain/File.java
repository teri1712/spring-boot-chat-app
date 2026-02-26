package com.decade.practice.inbox.domain;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

import static com.decade.practice.inbox.domain.File.FILE_TYPE;

@Entity
@DiscriminatorValue(FILE_TYPE)
@Getter
@NoArgsConstructor
public class File extends Message {
      private String filename;
      // TODO: Adjust client
      private String uri;
      private Integer size;

      public File(UUID chatEventId, UUID senderId, String chatId, Instant createdAt, String filename, Integer size, String uri) {
            super(chatEventId, senderId, chatId, createdAt, FILE_TYPE);
            this.filename = filename;
            this.size = size;
            this.uri = uri;
      }

      public static final String FILE_TYPE = "FILE";

      @Override
      public MessageState getState() {
            return FileState.builder()
                      .id(getSequenceId())
                      .chatEventId(getChatEventId())
                      .senderId(getSenderId())
                      .messageType(getMessageType())
                      .chatId(getChatId())
                      .createdAt(getCreatedAt())
                      .seenByIds(getSeenPointers().keySet())
                      .filename(getFilename())
                      .uri(getUri())
                      .size(getSize())
                      .build()
                      ;
      }
}
