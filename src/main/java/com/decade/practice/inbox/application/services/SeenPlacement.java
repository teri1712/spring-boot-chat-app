package com.decade.practice.inbox.application.services;

import com.decade.practice.inbox.application.ports.in.SeenCommand;
import com.decade.practice.inbox.application.ports.out.RoomEventRepository;
import com.decade.practice.inbox.application.ports.out.RoomRepository;
import com.decade.practice.inbox.domain.RoomEvent;
import com.decade.practice.inbox.domain.SeenRoomEvent;
import com.decade.practice.inbox.dto.mapper.ChatEventMapper;
import org.springframework.stereotype.Service;

@Service
public class SeenPlacement extends AbstractParticipantPlacement<SeenCommand> {

      public SeenPlacement(RoomEventRepository events, RoomRepository rooms, ChatEventMapper chatEventMapper) {
            super(events, rooms, chatEventMapper);
      }

      @Override
      protected RoomEvent newInstance(SeenCommand participantCommand) {
            return new SeenRoomEvent(
                      participantCommand.getPostingId(),
                      participantCommand.getChatId(),
                      participantCommand.getSenderId(),
                      participantCommand.getAt()
            );
      }
}
