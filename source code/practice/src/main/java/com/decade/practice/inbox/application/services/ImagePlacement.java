package com.decade.practice.inbox.application.services;

import com.decade.practice.inbox.application.ports.in.ImageCommand;
import com.decade.practice.inbox.application.ports.out.ChatEventRepository;
import com.decade.practice.inbox.domain.ChatEvent;
import com.decade.practice.inbox.domain.ImageChatEvent;
import com.decade.practice.inbox.dto.mapper.ChatEventMapper;
import org.springframework.stereotype.Service;

@Service
public class ImagePlacement extends AbstractParticipantPlacement<ImageCommand> {


      public ImagePlacement(ChatEventRepository events, ChatEventMapper chatEventMapper) {
            super(events, chatEventMapper);
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
