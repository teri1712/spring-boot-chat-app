package com.decade.practice.inbox.application.services;

import com.decade.practice.inbox.application.ports.in.FileCommand;
import com.decade.practice.inbox.application.ports.out.RoomEventRepository;
import com.decade.practice.inbox.application.ports.out.RoomRepository;
import com.decade.practice.inbox.domain.FileRoomEvent;
import com.decade.practice.inbox.domain.RoomEvent;
import com.decade.practice.inbox.dto.mapper.ChatEventMapper;
import org.springframework.stereotype.Service;

@Service
public class FilePlacement extends AbstractParticipantPlacement<FileCommand> {


      public FilePlacement(RoomEventRepository events, RoomRepository rooms, ChatEventMapper chatEventMapper) {
            super(events, rooms, chatEventMapper);
      }

      @Override
      protected RoomEvent newInstance(FileCommand participantCommand) {
            return new FileRoomEvent(
                      participantCommand.getPostingId(),
                      participantCommand.getChatId(),
                      participantCommand.getSenderId(),
                      participantCommand.getUri(),
                      participantCommand.getFilename(),
                      participantCommand.getSize()
            );
      }
}
