package com.decade.practice.inbox.application.services;

import com.decade.practice.inbox.apis.ConversationApi;
import com.decade.practice.inbox.application.ports.out.ConversationRepository;
import com.decade.practice.inbox.application.ports.out.RoomRepository;
import com.decade.practice.inbox.domain.Conversation;
import com.decade.practice.inbox.domain.Room;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(propagation = Propagation.MANDATORY)
public class ConversationApiImpl implements ConversationApi {

      private final ConversationRepository conversations;
      private final RoomRepository rooms;


      @Override
      public void create(String chatId, UUID caller, Set<UUID> participants, String name) {
            List<Conversation> conversationList = participants.stream()
                      .map(participant -> {
                            return new Conversation(chatId, participant);
                      }).toList();
            Room room = new Room(chatId, caller, name, null, participants);
            rooms.save(room);
            conversations.saveAll(conversationList);
      }
}
