package com.decade.practice.inbox.application.services;

import com.decade.practice.inbox.application.ports.in.SeenCommand;
import com.decade.practice.inbox.application.ports.out.ChatEventRepository;
import com.decade.practice.inbox.domain.ChatEvent;
import com.decade.practice.inbox.domain.SeenChatEvent;
import com.decade.practice.inbox.dto.mapper.ChatEventMapper;
import org.springframework.stereotype.Service;

@Service
public class SeenPlacement extends AbstractParticipantPlacement<SeenCommand> {

      public SeenPlacement(ChatEventRepository events, ChatEventMapper chatEventMapper) {
            super(events, chatEventMapper);
      }

      @Override
      protected ChatEvent newInstance(SeenCommand participantCommand) {
            return new SeenChatEvent(
                      participantCommand.getIdempotentKey(),
                      participantCommand.getChatId(),
                      participantCommand.getSenderId(),
                      participantCommand.getAt()
            );
      }
}
