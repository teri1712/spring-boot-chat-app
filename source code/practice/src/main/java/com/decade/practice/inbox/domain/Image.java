package com.decade.practice.inbox.domain;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

import static com.decade.practice.inbox.domain.Image.IMAGE_TYPE;

@Entity
@DiscriminatorValue(IMAGE_TYPE)
@Getter
@NoArgsConstructor
public class Image extends Message {

      @Valid
      @Column(updatable = false)
      @Embedded
      private ImageSpec image;

      public Image(UUID chatEventId, UUID senderId, String chatId, Instant createdAt, ImageSpec image) {
            super(chatEventId, senderId, chatId, createdAt, IMAGE_TYPE);
            this.image = image;
      }

      public static final String IMAGE_TYPE = "IMAGE";

      @Override
      public MessageState getState() {
            return ImageState.builder()
                      .sequenceId(getSequenceId())
                      .chatEventId(getChatEventId())
                      .senderId(getSenderId())
                      .messageType(getMessageType())
                      .chatId(getChatId())
                      .createdAt(getCreatedAt())
                      .seenByIds(getSeenPointers().keySet())
                      .image(getImage())
                      .build();
      }
}