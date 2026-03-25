package com.decade.practice.inbox.adapter;

import com.decade.practice.inbox.application.query.MessageService;
import com.decade.practice.inbox.dto.MessageStateResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping
@AllArgsConstructor
// TODO: refactor client
public class MessageController {

      private final MessageService messageService;

      @GetMapping("/chats/{chatId}/messages")
      public List<MessageStateResponse> listMessages(
                @AuthenticationPrincipal(expression = "id") UUID userId,
                @PathVariable String chatId,
                @RequestParam Long anchorSequenceNumber
      ) throws EntityNotFoundException {
            log.debug("Fetching messages for chat: {} at anchor {} by user {}", chatId, anchorSequenceNumber, userId);
            return messageService.findByChatAndSequenceLessThanEqual(chatId, userId, anchorSequenceNumber);
      }

}