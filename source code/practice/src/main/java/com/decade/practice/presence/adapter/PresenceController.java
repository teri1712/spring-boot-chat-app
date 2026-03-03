package com.decade.practice.presence.adapter;

import com.decade.practice.presence.application.query.PresenceService;
import com.decade.practice.presence.dto.ChatPresenceResponse;
import com.decade.practice.presence.dto.PresenceResponse;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@RestController
@AllArgsConstructor
@Validated
@RequestMapping
public class PresenceController {

      private final PresenceService presenceService;

      // TODO: Adjust client
      // TODO: Migrate to chat presence only
      @GetMapping("/me/presences")
      public List<PresenceResponse> listOnline(
                @AuthenticationPrincipal(expression = "id") UUID caller
      ) {
            return presenceService.getOnlineList(caller);
      }

      @GetMapping("/presences")
      public Map<String, ChatPresenceResponse> get(
                @AuthenticationPrincipal(expression = "id") UUID caller,
                @Size(min = 1, max = 50)
                @RequestParam(name = "chatId")
                Set<String> chatIds) {
            return presenceService.find(caller, chatIds);
      }
}