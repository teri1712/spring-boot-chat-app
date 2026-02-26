package com.decade.practice.inbox.adapter;

import com.decade.practice.inbox.application.query.ConversationService;
import com.decade.practice.inbox.dto.ConversationResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@AllArgsConstructor
public class ConversationController {

      private final ConversationService conversationService;

      // TODO: Adjust client and test
      @GetMapping("/me/conversations")
      public List<ConversationResponse> list(
                @AuthenticationPrincipal(expression = "id") UUID userId,
                @RequestParam Optional<Long> anchor
      ) throws Throwable {
            return conversationService.list(userId, anchor);
      }
}