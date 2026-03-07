package com.decade.practice.inbox.application.services;

import com.decade.practice.inbox.apis.ConversationApi;
import com.decade.practice.inbox.application.ports.out.ConversationRepository;
import com.decade.practice.inbox.domain.Conversation;
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


      @Override
      public void create(String chatId, Set<UUID> participants, String name) {
            List<Conversation> conversationList = participants.stream()
                      .map(participant -> {
                            return new Conversation(chatId, participant, name, null);
                      }).toList();

            conversations.saveAll(conversationList);
      }
}
