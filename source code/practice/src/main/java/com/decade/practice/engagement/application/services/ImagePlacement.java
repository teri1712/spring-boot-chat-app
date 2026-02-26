package com.decade.practice.engagement.application.services;

import com.decade.practice.engagement.application.ports.in.ImageCommand;
import com.decade.practice.engagement.application.ports.out.ChatEventRepository;
import com.decade.practice.engagement.application.ports.out.ChatRepository;
import com.decade.practice.engagement.application.ports.out.ParticipantRepository;
import com.decade.practice.engagement.domain.ChatEvent;
import com.decade.practice.engagement.domain.ImageChatEvent;
import com.decade.practice.engagement.domain.services.EngagementPolicy;
import com.decade.practice.engagement.dto.mapper.ChatEventMapper;
import org.springframework.stereotype.Service;

@Service
public class ImagePlacement extends AbstractParticipantPlacement<ImageCommand> {


      public ImagePlacement(ChatEventRepository events, ChatEventMapper chatEventMapper, ParticipantRepository participants, EngagementPolicy engagementPolicy, ChatRepository chats) {
            super(events, chatEventMapper, participants, engagementPolicy, chats);
      }

      @Override
      protected ChatEvent newInstance(ImageCommand participantCommand) {
            return new ImageChatEvent(
                      participantCommand.getIdempotentKey(),
                      participantCommand.getChatId(),
                      participantCommand.getSenderId(),
                      participantCommand.getUri(),
                      participantCommand.getWidth(),
                      participantCommand.getHeight(),
                      participantCommand.getFilename(),
                      participantCommand.getFormat()
            );
      }
}
