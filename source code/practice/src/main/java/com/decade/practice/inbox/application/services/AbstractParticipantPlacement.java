package com.decade.practice.inbox.application.services;

import com.decade.practice.inbox.application.MessageAlreadySentException;
import com.decade.practice.inbox.application.ports.in.ParticipantCommand;
import com.decade.practice.inbox.application.ports.in.ParticipantPlacement;
import com.decade.practice.inbox.application.ports.out.ChatEventRepository;
import com.decade.practice.inbox.domain.ChatEvent;
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

      private final ChatEventRepository events;
      private final ChatEventMapper chatEventMapper;


      private void doSave(ChatEvent chatEvent) {
            try {
                  events.saveAndFlush(chatEvent.getChatId(), chatEvent.getSenderId(), chatEvent);
            } catch (DataIntegrityViolationException e) {
                  throw new MessageAlreadySentException(chatEvent.getId());
            }
      }

      @Override
      public ChatEventResponse place(Command participantCommand) {
            ChatEvent chatEvent = newInstance(participantCommand);
            doSave(chatEvent);
            return chatEventMapper.toResponse(chatEvent);
      }

      protected abstract ChatEvent newInstance(Command participantCommand);
}
