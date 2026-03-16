package com.decade.practice.chatorchestrator.application.services;

import com.decade.practice.chatorchestrator.application.ports.in.ChatService;
import com.decade.practice.chatorchestrator.application.ports.in.CreateGroupChatCommand;
import com.decade.practice.chatorchestrator.dto.ChatResponse;
import com.decade.practice.chatorchestrator.dto.DirectChatResponse;
import com.decade.practice.chatorchestrator.dto.mappers.ChatMapper;
import com.decade.practice.chatsettings.api.SettingApi;
import com.decade.practice.chatsettings.api.SettingsInfo;
import com.decade.practice.engagement.api.ChatPolicyInfo;
import com.decade.practice.engagement.api.DirectInfo;
import com.decade.practice.engagement.api.EngagementApi;
import com.decade.practice.inbox.apis.ConversationApi;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
// TODO: SAGA
public class ChatOrchestrator implements ChatService {

      private final EngagementApi engagementApi;
      private final SettingApi settingApi;
      private final ConversationApi conversationApi;
      private final ChatMapper chatMapper;

      @Override
      public ChatResponse createGroup(CreateGroupChatCommand command) {
            ChatPolicyInfo policyInfo = engagementApi.createGroup(command.callerId(), command.partnerIds());
            SettingsInfo settingsInfo = settingApi.create(policyInfo.identifier(), command.roomName());
            conversationApi.create(policyInfo.identifier(), command.callerId(), policyInfo.creators(), command.roomName());
            return chatMapper.map(policyInfo, settingsInfo);
      }

      @Override
      public DirectChatResponse getDirect(UUID callerId, UUID partnerId) {
            return engagementApi.findDirectMapping(callerId, partnerId)
                      .map(mapping -> new DirectChatResponse(mapping, false))
                      .orElseGet(() -> {
                            DirectInfo direct = engagementApi.createDirect(callerId, partnerId);
                            ChatPolicyInfo policy = direct.policy();
                            settingApi.create(policy.identifier(), null);
                            conversationApi.create(policy.identifier(), callerId, new HashSet<>(List.of(callerId, partnerId)), null);
                            return new DirectChatResponse(direct.mapping(), true);
                      });
      }

      @Override
      public ChatResponse find(String chatId, UUID userId) {
            ChatPolicyInfo policyInfo = engagementApi.find(chatId, userId).orElseThrow();
            SettingsInfo settingsInfo = settingApi.find(Set.of(chatId)).get(chatId);
            return chatMapper.map(policyInfo, settingsInfo);
      }
}
