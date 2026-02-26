package com.decade.practice.engagement.application.ports.in;

import lombok.Getter;

import java.util.UUID;

@Getter
public class ImageCommand extends ParticipantCommand {

      private final String uri;
      private final Integer width;
      private final Integer height;
      private final String filename;
      private final String format;

      public ImageCommand(String chatId, UUID senderId, UUID idempotentKey, String uri, Integer width, Integer height, String filename, String format) {
            super(chatId, senderId, idempotentKey);
            this.uri = uri;
            this.width = width;
            this.height = height;
            this.filename = filename;
            this.format = format;
      }
}
