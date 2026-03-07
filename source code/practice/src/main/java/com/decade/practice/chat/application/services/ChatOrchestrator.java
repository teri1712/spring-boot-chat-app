package com.decade.practice.chat.application.services;

import com.decade.practice.chat.application.ports.in.ChatService;
import com.decade.practice.chat.application.ports.in.CreateGroupChatCommand;
import com.decade.practice.chat.dto.ChatResponse;
import com.decade.practice.chat.dto.DirectChatResponse;
import com.decade.practice.chatsettings.api.SettingApi;
import com.decade.practice.chatsettings.api.SettingsInfo;
import com.decade.practice.engagement.api.ChatPolicyInfo;
import com.decade.practice.engagement.api.EngagementApi;
import com.decade.practice.inbox.apis.ConversationApi;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
// TODO: SAGA
public class ChatOrchestrator implements ChatService {

      private final EngagementApi engagementApi;
      private final SettingApi settingApi;
      private final ConversationApi conversationApi;

      @Override
      public ChatResponse createGroup(CreateGroupChatCommand command) {
            ChatPolicyInfo policyInfo = engagementApi.createGroup(command.callerId(), command.partnerId());
            SettingsInfo settingsInfo = settingApi.create(policyInfo.identifier(), command.roomName());
            conversationApi.create(policyInfo.identifier(), new HashSet<>(List.of(command.callerId(), command.partnerId())), command.roomName());
            return new ChatResponse(policyInfo, settingsInfo);
      }

      @Override
      public DirectChatResponse getDirect(UUID callerId, UUID partnerId) {
            SettingsInfo settingsInfo;
            boolean newly = false;
            Optional<ChatPolicyInfo> policyInfo = engagementApi.findDirect(callerId, partnerId);
            if (policyInfo.isPresent()) {
                  String chatId = policyInfo.get().identifier();
                  settingsInfo = settingApi.find(Set.of(chatId)).get(chatId);
            } else {
                  newly = true;
                  ChatPolicyInfo policy = engagementApi.createDirect(callerId, partnerId);
                  settingsInfo = settingApi.create(policy.identifier(), null);
                  conversationApi.create(policy.identifier(), new HashSet<>(List.of(callerId, partnerId)), null);
                  policyInfo = Optional.of(policy);
            }
            return new DirectChatResponse(new ChatResponse(policyInfo.orElseThrow(), settingsInfo), newly);
      }

      @Override
      public ChatResponse find(String chatId, UUID userId) {
            ChatPolicyInfo policyInfo = engagementApi.find(chatId, userId).orElseThrow();
            SettingsInfo settingsInfo = settingApi.find(Set.of(chatId)).get(chatId);
            return new ChatResponse(policyInfo, settingsInfo);
      }
}
