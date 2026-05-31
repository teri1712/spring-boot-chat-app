package com.decade.practice.inbox.application.ports.in;

import com.decade.practice.resources.files.api.FileIntegrity;
import lombok.Getter;

import java.util.UUID;

@Getter
public class ImageCommand extends ParticipantCommand {

      private final FileIntegrity file;
      private final Integer width;
      private final Integer height;
      private final String filename;
      private final String format;

      public ImageCommand(String chatId, UUID senderId, UUID postingId, FileIntegrity file, Integer width, Integer height, String filename, String format) {
            super(chatId, senderId, postingId);
            this.file = file;
            this.width = width;
            this.height = height;
            this.filename = filename;
            this.format = format;
      }
}