package com.decade.practice.engagement.application.services;

import com.decade.practice.engagement.application.exceptions.MessageAlreadySentException;
import com.decade.practice.engagement.application.ports.in.ParticipantCommand;
import com.decade.practice.engagement.application.ports.in.ParticipantPlacement;
import com.decade.practice.engagement.application.ports.out.ChatEventRepository;
import com.decade.practice.engagement.application.ports.out.ChatRepository;
import com.decade.practice.engagement.application.ports.out.ParticipantRepository;
import com.decade.practice.engagement.domain.Chat;
import com.decade.practice.engagement.domain.ChatEvent;
import com.decade.practice.engagement.domain.Participant;
import com.decade.practice.engagement.domain.ParticipantId;
import com.decade.practice.engagement.domain.services.EngagementPolicy;
import com.decade.practice.engagement.dto.ChatEventResponse;
import com.decade.practice.engagement.dto.mapper.ChatEventMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;


@RequiredArgsConstructor
@Transactional
public abstract class AbstractParticipantPlacement<Command extends ParticipantCommand> implements ParticipantPlacement<Command> {

      private final ChatEventRepository events;
      private final ChatEventMapper chatEventMapper;
      private final ParticipantRepository participants;
      private final EngagementPolicy engagementPolicy;
      private final ChatRepository chats;


      private void doSave(ChatEvent chatEvent) {
            try {
                  events.saveAndFlush(chatEvent);
            } catch (DataIntegrityViolationException e) {
                  throw new MessageAlreadySentException(chatEvent.getId());
            }
      }

      @Override
      public ChatEventResponse place(Command participantCommand) {
            ChatEvent chatEvent = newInstance(participantCommand);
            Chat chat = chats.findById(chatEvent.getChatId()).orElseThrow();
            Participant participant = participants.findById(new ParticipantId(chatEvent.getSenderId(), chatEvent.getChatId())).orElse(null);
            engagementPolicy.applyWrite(participant, chat);
            doSave(chatEvent);

            chat.increment(chatEvent.getId());
            chats.save(chat);
            return chatEventMapper.toResponse(chatEvent);
      }

      protected abstract ChatEvent newInstance(Command participantCommand);
}
