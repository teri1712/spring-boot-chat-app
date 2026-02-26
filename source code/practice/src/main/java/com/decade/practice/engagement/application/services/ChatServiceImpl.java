package com.decade.practice.engagement.application.services;

import com.decade.practice.engagement.application.ports.out.ChatRepository;
import com.decade.practice.engagement.application.ports.out.ParticipantRepository;
import com.decade.practice.engagement.application.query.ChatService;
import com.decade.practice.engagement.domain.Chat;
import com.decade.practice.engagement.domain.Participant;
import com.decade.practice.engagement.domain.ParticipantId;
import com.decade.practice.engagement.domain.services.EngagementPolicy;
import com.decade.practice.engagement.dto.ChatResponse;
import com.decade.practice.engagement.dto.mapper.ChatMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service("chatService")
@RequiredArgsConstructor
@Transactional
public class ChatServiceImpl implements ChatService {

      private final ChatRepository chatRepo;
      private final ChatMapper chatMapper;
      private final EngagementPolicy engagementPolicy;
      private final ParticipantRepository participants;


      @Override
      public ChatResponse find(String chatId, UUID userId) {
            Participant participant = participants.findById(new ParticipantId(userId, chatId)).orElse(null);
            Chat chat = chatRepo.findById(chatId).orElseThrow();
            engagementPolicy.applyRead(participant, chat);
            return chatMapper.toResponse(chat, false);
      }

}
