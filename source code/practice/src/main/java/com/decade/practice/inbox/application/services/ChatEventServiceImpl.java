package com.decade.practice.inbox.application.services;

import com.decade.practice.inbox.application.ports.out.ChatEventRepository;
import com.decade.practice.inbox.application.query.ChatEventService;
import com.decade.practice.inbox.dto.ChatEventResponse;
import com.decade.practice.inbox.dto.mapper.ChatEventMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class ChatEventServiceImpl implements ChatEventService {

      private final ChatEventRepository receipts;
      private final ChatEventMapper chatEventMapper;


      @Override
      public ChatEventResponse find(UUID idempotentKey) {
            return chatEventMapper.toResponse(receipts.findById(idempotentKey).orElseThrow());
      }

}
