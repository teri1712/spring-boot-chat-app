package com.decade.practice.engagement.application.services;

import com.decade.practice.engagement.api.*;
import com.decade.practice.engagement.api.mapper.ChatPolicyMapper;
import com.decade.practice.engagement.application.ports.in.EngagementService;
import com.decade.practice.engagement.application.ports.out.ChatPolicyRepository;
import com.decade.practice.engagement.application.ports.out.ParticipantRepository;
import com.decade.practice.engagement.domain.*;
import com.decade.practice.engagement.domain.services.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
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
      private final StalkPolicy stalkPolicy;


      @Override
      public Optional<ChatPolicyInfo> find(String chatId, UUID userId) {
            Participant participant = participants.findById(new ParticipantId(userId, chatId)).orElse(null);
            Chat chat = chatPolicies.findById(chatId).orElse(null);
            engagementPolicy.applyRead(participant, chat);
            return Optional.ofNullable(chat).map(chatMapper::map);
      }

      @Override
//      @Cacheable(cacheNames = "directMapping", key = "#userId + '_' + #partnerId", unless = "#result.isEmpty()")
      public Optional<DirectMapping> findDirectMapping(UUID userId, UUID partnerId) {
            String chatId = directChatFactory.make(new ChatCreators(userId, Set.of(partnerId)));
            return chatPolicies.findById(chatId).map(new Function<Chat, DirectMapping>() {
                  @Override
                  public DirectMapping apply(Chat chat) {
                        Participant participant = participants.findById(new ParticipantId(userId, chatId)).orElse(null);
                        engagementPolicy.applyRead(participant, chat);
                        stalkPolicy.apply(userId, partnerId);
                        return new DirectMapping(userId, partnerId, chat.getChatId());
                  }
            });
      }

      @Override
      public void add(String chatId, UUID partnerId) {

            Chat chat = chatPolicies.findById(chatId).orElseThrow();
            Participant participant = new Participant(partnerId, chatId);
            chatPolicyService.apply(participant, chat);
            participants.save(participant);

      }

      @Override
      public ChatPolicyInfo createGroup(UUID callerId, Set<UUID> participants) {
            ChatCreators creators = new ChatCreators(callerId, participants);
            Chat chat = groupChatFactory.create(creators, 1000);
            doSave(chat, creators.getMembers());
            return chatMapper.map(chat);
      }

      private void doSave(Chat policy, Set<UUID> participants) {
            participants.forEach(participantId -> {
                  Participant participant = new Participant(participantId, policy.getChatId());
                  this.participants.save(participant);
            });
            chatPolicies.saveAndFlush(policy);
      }

      @Override
      public DirectInfo createDirect(UUID callerId, UUID partnerId) {
            ChatCreators creators = new ChatCreators(callerId, Set.of(partnerId));
            Chat chat = directChatFactory.create(creators);
            try {
                  doSave(chat, Stream.of(callerId, partnerId).collect(Collectors.toSet()));
                  stalkPolicy.apply(callerId, partnerId);
                  return new DirectInfo(chatMapper.map(chat), new DirectMapping(callerId, partnerId, chat.getChatId()));
            } catch (DataIntegrityViolationException e) {
                  log.debug("Concurrent insert encountered for chat: {}", chat.getChatId(), e);
                  throw new ChatIdentifierUniqueException(chat.getChatId());
            }

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
