package com.decade.practice.inbox.adapter;

import com.decade.practice.inbox.application.query.LogService;
import com.decade.practice.inbox.dto.InboxLogResponse;
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
public class LogController {

      private final LogService logService;

      @GetMapping("/chats/{chatId}/logs")
      public List<InboxLogResponse> listLog(
                @AuthenticationPrincipal(expression = "id") UUID userId,
                @PathVariable @Validated String chatId,
                @RequestParam Long anchorSequenceId
      ) throws EntityNotFoundException {
            return logService.findByChatAndSequenceLessThanEqual(userId, chatId, anchorSequenceId);
      }

      // TODO: Adjust client to last currentState
      @GetMapping("/users/me/logs")
      public List<InboxLogResponse> listLog(
                @AuthenticationPrincipal(expression = "id") UUID userId,
                @RequestParam Long anchorSequenceId
      ) throws EntityNotFoundException {
            return logService.findBySequenceLessThanEqual(userId, anchorSequenceId);
      }
}