package com.decade.practice.inbox.application.services;

import com.decade.practice.inbox.application.ports.in.ImageCommand;
import com.decade.practice.inbox.application.ports.out.RoomEventRepository;
import com.decade.practice.inbox.application.ports.out.RoomRepository;
import com.decade.practice.inbox.domain.ImageRoomEvent;
import com.decade.practice.inbox.domain.RoomEvent;
import com.decade.practice.inbox.dto.mapper.ChatEventMapper;
import org.springframework.stereotype.Service;

@Service
public class ImagePlacement extends AbstractParticipantPlacement<ImageCommand> {


      public ImagePlacement(RoomEventRepository events, RoomRepository rooms, ChatEventMapper chatEventMapper) {
            super(events, rooms, chatEventMapper);
      }

      @Override
      protected RoomEvent newInstance(ImageCommand participantCommand) {
            return new ImageRoomEvent(
                      participantCommand.getPostingId(),
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
