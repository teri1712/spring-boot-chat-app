package com.decade.practice.engagement.application.events;


import com.decade.practice.engagement.application.ports.out.ChatEventRepository;
import com.decade.practice.engagement.application.ports.out.ChatRepository;
import com.decade.practice.engagement.application.ports.out.ParticipantRepository;
import com.decade.practice.engagement.domain.*;
import com.decade.practice.engagement.domain.events.ChatEventPlaced;
import com.decade.practice.engagement.domain.events.VersionIncremented;
import com.decade.practice.engagement.domain.services.EngagementPolicy;
import com.decade.practice.engagement.dto.events.EventPlacedMapper;
import com.decade.practice.engagement.dto.events.IntegrationChatCreated;
import com.decade.practice.engagement.dto.events.IntegrationChatSnapshot;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Service
public class ChatEventListener {

      private final ChatRepository chats;
      private final ChatEventRepository events;
      private final ParticipantRepository participants;
      private final ApplicationEventPublisher publisher;
      private final EngagementPolicy engagementPolicy;
      // My bad, I'm lazy
      protected final EventPlacedMapper mapper;

      @EventListener
      public void on(ChatCreated chatCreated) {
            IntegrationChatCreated eventPlaced = IntegrationChatCreated.builder()
                      .createdAt(Instant.now())
                      .senderId(chatCreated.callerId())
                      .snapshot(new IntegrationChatSnapshot(chatCreated.chatId(),
                                chatCreated.roomName(),
                                chatCreated.roomAvatar(),
                                chatCreated.creators(),
                                chatCreated.participants()))
                      .build();
            publisher.publishEvent(eventPlaced);
      }

      @ApplicationModuleListener
      public void on(ChatEventPlaced eventCreated) {
            String chatId = eventCreated.chatId();

            ParticipantId participantId = new ParticipantId(eventCreated.senderId(), chatId);
            Participant participant = participants.findById(participantId).orElseThrow();

            Chat chat = chats.findByIdIncrementVersion(chatId).orElseThrow();
            engagementPolicy.applyWrite(participant, chat);
            chat.increment(eventCreated.id());
            chats.save(chat);
      }

      @EventListener
      public void on(VersionIncremented eventIncremented) {
            ChatEvent chatEvent = events.findById(eventIncremented.eventId()).orElseThrow();
            chatEvent.accept(eventIncremented.eventVersion());

            String chatId = eventIncremented.chatId();
            Chat chat = chats.findById(chatId).orElseThrow();
            Preference preference = chat.getPreference();
            String roomName = preference.roomName();
            String roomAvatar = preference.roomAvatar();
            List<UUID> peers = participants.findByChatId(chatId);
            IntegrationChatSnapshot snapshot = new IntegrationChatSnapshot(chatId, roomName, roomAvatar,
                      chat.getCreators().members().toList(),
                      peers);
            publisher.publishEvent(mapper.map(chatEvent, snapshot));
            // TODO: Handle fanout
            events.save(chatEvent);
      }


}
