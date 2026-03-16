package com.decade.practice.inbox.application.services;

import com.decade.practice.inbox.application.MessageAlreadySentException;
import com.decade.practice.inbox.application.ports.in.ParticipantCommand;
import com.decade.practice.inbox.application.ports.in.ParticipantPlacement;
import com.decade.practice.inbox.application.ports.out.RoomEventRepository;
import com.decade.practice.inbox.application.ports.out.RoomRepository;
import com.decade.practice.inbox.domain.Room;
import com.decade.practice.inbox.domain.RoomEvent;
import com.decade.practice.inbox.dto.ChatEventResponse;
import com.decade.practice.inbox.dto.mapper.ChatEventMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@RequiredArgsConstructor
@Transactional
public abstract class AbstractParticipantPlacement<Command extends ParticipantCommand> implements ParticipantPlacement<Command> {

      private final RoomEventRepository events;
      private final RoomRepository rooms;
      private final ChatEventMapper chatEventMapper;


      private void doSave(RoomEvent roomEvent) {
            try {
                  events.saveAndFlush(roomEvent.getChatId(), roomEvent.getSenderId(), roomEvent);
            } catch (DataIntegrityViolationException e) {
                  throw new MessageAlreadySentException(roomEvent.getPostingId());
            }
      }

      @Override
      public ChatEventResponse place(Command participantCommand) {
            RoomEvent roomEvent = newInstance(participantCommand);
            Room room = rooms.findById(participantCommand.getChatId()).orElseThrow();
            room.refreshLastActivity();
            rooms.save(room);
            doSave(roomEvent);
            return chatEventMapper.toResponse(roomEvent);
      }

      protected abstract RoomEvent newInstance(Command participantCommand);
}
