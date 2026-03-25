package com.decade.practice.inbox.application.services;

import com.decade.practice.inbox.application.ports.in.TextCommand;
import com.decade.practice.inbox.application.ports.out.RoomEventRepository;
import com.decade.practice.inbox.application.ports.out.RoomRepository;
import com.decade.practice.inbox.domain.RoomEvent;
import com.decade.practice.inbox.domain.TextRoomEvent;
import com.decade.practice.inbox.dto.mapper.ChatEventMapper;
import org.springframework.stereotype.Service;

@Service
public class TextPlacement extends AbstractParticipantPlacement<TextCommand> {


      public TextPlacement(RoomEventRepository events, RoomRepository rooms, ChatEventMapper chatEventMapper) {
            super(events, rooms, chatEventMapper);
      }

      @Override
      protected RoomEvent newInstance(TextCommand participantCommand) {
            return new TextRoomEvent(
                      participantCommand.getPostingId(),
                      participantCommand.getChatId(),
                      participantCommand.getSenderId(),
                      participantCommand.getContent()
            );
      }
}
