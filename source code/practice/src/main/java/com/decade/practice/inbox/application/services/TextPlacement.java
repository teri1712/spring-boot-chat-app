package com.decade.practice.inbox.application.services;

import com.decade.practice.inbox.application.ports.in.TextCommand;
import com.decade.practice.inbox.application.ports.out.ChatEventRepository;
import com.decade.practice.inbox.domain.ChatEvent;
import com.decade.practice.inbox.domain.TextChatEvent;
import com.decade.practice.inbox.dto.mapper.ChatEventMapper;
import org.springframework.stereotype.Service;

@Service
public class TextPlacement extends AbstractParticipantPlacement<TextCommand> {


      public TextPlacement(ChatEventRepository events, ChatEventMapper chatEventMapper) {
            super(events, chatEventMapper);
      }

      @Override
      protected ChatEvent newInstance(TextCommand participantCommand) {
            return new TextChatEvent(
                      participantCommand.getIdempotentKey(),
                      participantCommand.getChatId(),
                      participantCommand.getSenderId(),
                      participantCommand.getContent()
            );
      }
}
