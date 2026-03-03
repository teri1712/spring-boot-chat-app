package com.decade.practice.inbox.adapter;

import com.decade.practice.inbox.application.query.MessageService;
import com.decade.practice.inbox.dto.MessageStateResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping
@AllArgsConstructor
// TODO: refactor client
public class MessageController {

      private final MessageService messageService;

      @GetMapping("/chats/{chatId}/messages")
      public List<MessageStateResponse> listMessages(
                @AuthenticationPrincipal(expression = "id") UUID userId,
                @PathVariable @Validated String chatId,
                @RequestParam Long anchorSequenceNumber
      ) throws EntityNotFoundException {
            return messageService.findByChatAndSequenceLessThanEqual(userId, chatId, anchorSequenceNumber);
      }

}