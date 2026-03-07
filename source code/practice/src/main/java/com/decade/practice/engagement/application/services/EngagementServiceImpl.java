package com.decade.practice.engagement.application.services;

import com.decade.practice.engagement.api.ChatIdentifierUniqueException;
import com.decade.practice.engagement.api.ChatPolicyInfo;
import com.decade.practice.engagement.api.EngagementApi;
import com.decade.practice.engagement.api.mapper.ChatPolicyMapper;
import com.decade.practice.engagement.application.ports.in.EngagementService;
import com.decade.practice.engagement.application.ports.out.ChatPolicyRepository;
import com.decade.practice.engagement.application.ports.out.ParticipantRepository;
import com.decade.practice.engagement.domain.*;
import com.decade.practice.engagement.domain.services.ChatPolicyService;
import com.decade.practice.engagement.domain.services.DirectChatFactory;
import com.decade.practice.engagement.domain.services.EngagementPolicy;
import com.decade.practice.engagement.domain.services.GroupChatFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service("engagementApi")
@Slf4j
@Transactional
@RequiredArgsConstructor
public class EngagementServiceImpl implements EngagementService, EngagementApi {

      private final ParticipantRepository participants;
      private final ChatPolicyRepository chatPolicies;
      private final ChatPolicyService chatPolicyService;


      private final GroupChatFactory groupChatFactory;
      private final DirectChatFactory directChatFactory;
      private final EngagementPolicy engagementPolicy;

      private final ChatPolicyMapper chatMapper;


      @Override
      public Optional<ChatPolicyInfo> find(String chatId, UUID userId) {
            Participant participant = participants.findById(new ParticipantId(userId, chatId)).orElse(null);
            Chat chat = chatPolicies.findById(chatId).orElse(null);
            engagementPolicy.applyRead(participant, chat);
            return Optional.ofNullable(chat).map(chatMapper::map);
      }

      @Override
      public Optional<ChatPolicyInfo> findDirect(UUID userId, UUID partnerId) {
            String chatId = directChatFactory.make(new ChatCreators(userId, partnerId));
            Participant participant = participants.findById(new ParticipantId(userId, chatId)).orElse(null);
            Chat chat = chatPolicies.findById(chatId).orElse(null);
            engagementPolicy.applyRead(participant, chat);
            return Optional.ofNullable(chat).map(chatMapper::map);
      }

      @Override
      public void add(String chatId, UUID partnerId) {

            Chat chat = chatPolicies.findById(chatId).orElseThrow();
            Participant participant = new Participant(partnerId, chatId);
            chatPolicyService.apply(participant, chat);
            participants.save(participant);

      }

      @Override
      @Transactional(propagation = Propagation.MANDATORY)
      public ChatPolicyInfo createGroup(UUID callerId, UUID partnerId) {
            ChatCreators creators = new ChatCreators(callerId, partnerId);
            Chat chat = groupChatFactory.create(creators, 1000);
            doSave(chat, Stream.of(callerId, partnerId).collect(Collectors.toSet()));
            return chatMapper.map(chat);
      }

      private void doSave(Chat policy, Set<UUID> participantsStream) {
            participantsStream.forEach(participantId -> {
                  Participant participant = new Participant(participantId, policy.getChatId());
                  participants.save(participant);
            });
            chatPolicies.saveAndFlush(policy);
      }

      @Override
      @Transactional(propagation = Propagation.MANDATORY)
      public ChatPolicyInfo createDirect(UUID callerId, UUID partnerId) {
            ChatCreators creators = new ChatCreators(callerId, partnerId);

            return chatPolicies.findById(directChatFactory.make(creators))
                      .map(chatMapper::map).orElseGet(() -> {
                            Chat policy = directChatFactory.create(creators);
                            try {
                                  doSave(policy, Stream.of(callerId, partnerId).collect(Collectors.toSet()));
                                  return chatMapper.map(policy);
                            } catch (DataIntegrityViolationException e) {
                                  log.debug("Concurrent insert encountered for chat: {}", policy.getChatId(), e);
                                  throw new ChatIdentifierUniqueException(policy.getChatId());
                            }
                      });

      }


      @Override
      public boolean canRead(String chatId, UUID userId) {
            return participants.findById(new ParticipantId(userId, chatId))
                      .map(Participant::getParticipantPolicy)
                      .map(ParticipantPolicy::read)
                      .orElse(false);
      }

      @Override
      public boolean canWrite(String chatId, UUID userId) {
            return participants.findById(new ParticipantId(userId, chatId))
                      .map(Participant::getParticipantPolicy)
                      .map(ParticipantPolicy::write)
                      .orElse(false);
      }

}
