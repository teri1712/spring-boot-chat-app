package com.decade.practice.inbox.application.events;

import com.decade.practice.inbox.application.ports.out.ConversationRepository;
import com.decade.practice.inbox.application.ports.out.DeliveryService;
import com.decade.practice.inbox.application.ports.out.LogRepository;
import com.decade.practice.inbox.application.ports.out.LookUpRegistry;
import com.decade.practice.inbox.domain.services.ConversationInfoService;
import com.decade.practice.inbox.dto.mapper.InboxLogMapper;
import org.springframework.stereotype.Service;

@Service
public class Batch2040RoundRobinSaver extends BatchRoundRobinSaver {
      public Batch2040RoundRobinSaver(LogRepository logs, ConversationRepository conversations, LookUpRegistry lookUpRegistry, ConversationInfoService conversationInfoService, DeliveryService deliveryService, InboxLogMapper inboxLogMapper) {
            super(20, 40, logs, conversations, lookUpRegistry, conversationInfoService, deliveryService, inboxLogMapper);
      }
}
