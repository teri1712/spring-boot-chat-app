package com.decade.practice.inbox.application.ports.in;

import com.decade.practice.resources.files.api.FileIntegrity;
import lombok.Getter;

import java.util.UUID;

@Getter
public class FileCommand extends ParticipantCommand {
      private final FileIntegrity file;
      private final String filename;
      private final Integer size;

      public FileCommand(String chatId, UUID senderId, UUID postingId, FileIntegrity file, String filename, Integer size) {
            super(chatId, senderId, postingId);
            this.file = file;
            this.filename = filename;
            this.size = size;
      }
}
