package com.decade.practice.inbox.application.services;

import com.decade.practice.inbox.application.ports.in.IconCommand;
import com.decade.practice.inbox.application.ports.out.RoomEventRepository;
import com.decade.practice.inbox.application.ports.out.RoomRepository;
import com.decade.practice.inbox.domain.IconRoomEvent;
import com.decade.practice.inbox.domain.RoomEvent;
import com.decade.practice.inbox.dto.mapper.ChatEventMapper;
import org.springframework.stereotype.Service;

@Service
public class IconPlacement extends AbstractParticipantPlacement<IconCommand> {


      public IconPlacement(RoomEventRepository events, RoomRepository rooms, ChatEventMapper chatEventMapper) {
            super(events, rooms, chatEventMapper);
      }

      @Override
      protected RoomEvent newInstance(IconCommand participantCommand) {
            return new IconRoomEvent(
                      participantCommand.getPostingId(),
                      participantCommand.getChatId(),
                      participantCommand.getSenderId(),
                      participantCommand.getIconId()
            );
      }
}
