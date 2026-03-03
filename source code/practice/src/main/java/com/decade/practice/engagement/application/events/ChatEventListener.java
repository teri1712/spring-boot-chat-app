package com.decade.practice.engagement.application.events;


import com.decade.practice.engagement.application.ports.out.ChatEventRepository;
import com.decade.practice.engagement.application.ports.out.ChatRepository;
import com.decade.practice.engagement.application.ports.out.ParticipantRepository;
import com.decade.practice.engagement.domain.Chat;
import com.decade.practice.engagement.domain.ChatEvent;
import com.decade.practice.engagement.domain.Preference;
import com.decade.practice.engagement.domain.events.ChatCreatedAccepted;
import com.decade.practice.engagement.domain.events.ChatSnapshot;
import com.decade.practice.engagement.domain.events.EventPlacedMapper;
import com.decade.practice.engagement.domain.events.VersionIncremented;
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
      protected final EventPlacedMapper mapper;

      @EventListener
      public void on(ChatCreated chatCreated) {
            ChatCreatedAccepted eventPlaced = ChatCreatedAccepted.builder()
                      .chatId(chatCreated.chatId())
                      .createdAt(Instant.now())
                      .senderId(chatCreated.callerId())
                      .snapshot(new ChatSnapshot(chatCreated.chatId(),
                                chatCreated.roomName(),
                                chatCreated.roomAvatar(),
                                chatCreated.creators(),
                                chatCreated.participants()))
                      .build();
            publisher.publishEvent(eventPlaced);
      }

      @ApplicationModuleListener
      // TODO: Ordering
      public void on(VersionIncremented eventIncremented) {
            ChatEvent chatEvent = events.findById(eventIncremented.eventId()).orElseThrow();
            chatEvent.accept(eventIncremented.eventVersion());

            String chatId = eventIncremented.chatId();
            Chat chat = chats.findById(chatId).orElseThrow();
            Preference preference = chat.getPreference();
            List<UUID> peers = participants.findByChatId(chatId);
            ChatSnapshot snapshot = new ChatSnapshot(
                      chatId,
                      preference.roomName(),
                      preference.roomAvatar(),
                      chat.getCreators().members().toList(),
                      peers);
            publisher.publishEvent(mapper.map(chatEvent, snapshot));
            // TODO: Handle fanout
            events.save(chatEvent);
      }


}
