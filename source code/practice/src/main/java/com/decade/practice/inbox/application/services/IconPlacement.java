package com.decade.practice.inbox.application.services;

import com.decade.practice.inbox.application.ports.in.IconCommand;
import com.decade.practice.inbox.application.ports.out.ChatEventRepository;
import com.decade.practice.inbox.domain.ChatEvent;
import com.decade.practice.inbox.domain.IconChatEvent;
import com.decade.practice.inbox.dto.mapper.ChatEventMapper;
import org.springframework.stereotype.Service;

@Service
public class IconPlacement extends AbstractParticipantPlacement<IconCommand> {


      public IconPlacement(ChatEventRepository events, ChatEventMapper chatEventMapper) {
            super(events, chatEventMapper);
      }

      @Override
      protected ChatEvent newInstance(IconCommand participantCommand) {
            return new IconChatEvent(
                      participantCommand.getIdempotentKey(),
                      participantCommand.getChatId(),
                      participantCommand.getSenderId(),
                      participantCommand.getIconId()
            );
      }
}
