package com.decade.practice.engagement.application.services;

import com.decade.practice.engagement.application.ports.in.IconCommand;
import com.decade.practice.engagement.application.ports.out.ChatEventRepository;
import com.decade.practice.engagement.application.ports.out.ChatRepository;
import com.decade.practice.engagement.application.ports.out.ParticipantRepository;
import com.decade.practice.engagement.domain.ChatEvent;
import com.decade.practice.engagement.domain.IconChatEvent;
import com.decade.practice.engagement.domain.services.EngagementPolicy;
import com.decade.practice.engagement.dto.mapper.ChatEventMapper;
import org.springframework.stereotype.Service;

@Service
public class IconPlacement extends AbstractParticipantPlacement<IconCommand> {


      public IconPlacement(ChatEventRepository events, ChatEventMapper chatEventMapper, ParticipantRepository participants, EngagementPolicy engagementPolicy, ChatRepository chats) {
            super(events, chatEventMapper, participants, engagementPolicy, chats);
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
