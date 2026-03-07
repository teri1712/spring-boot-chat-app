package com.decade.practice.inbox.application.services;

import com.decade.practice.inbox.application.ports.in.FileCommand;
import com.decade.practice.inbox.application.ports.out.ChatEventRepository;
import com.decade.practice.inbox.domain.ChatEvent;
import com.decade.practice.inbox.domain.FileChatEvent;
import com.decade.practice.inbox.dto.mapper.ChatEventMapper;
import org.springframework.stereotype.Service;

@Service
public class FilePlacement extends AbstractParticipantPlacement<FileCommand> {


      public FilePlacement(ChatEventRepository events, ChatEventMapper chatEventMapper) {
            super(events, chatEventMapper);
      }

      @Override
      protected ChatEvent newInstance(FileCommand participantCommand) {
            return new FileChatEvent(
                      participantCommand.getIdempotentKey(),
                      participantCommand.getChatId(),
                      participantCommand.getSenderId(),
                      participantCommand.getUri(),
                      participantCommand.getFilename(),
                      participantCommand.getSize()
            );
      }
}
