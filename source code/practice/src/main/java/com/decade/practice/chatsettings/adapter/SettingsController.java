package com.decade.practice.chatsettings.adapter;

import com.decade.practice.chatsettings.application.services.SettingsService;
import com.decade.practice.chatsettings.dto.PreferenceRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@AllArgsConstructor
public class SettingsController {

      private final SettingsService settingsService;

      // TODO: Adjust client to patch
      @PatchMapping("/chats/{identifier}/preference")
      @ResponseStatus(HttpStatus.ACCEPTED)
      public void setPreference(
                @AuthenticationPrincipal(expression = "id") UUID userId,
                @PathVariable String identifier,
                @Valid @RequestBody PreferenceRequest request) {

            settingsService.setPreference(identifier, userId, request);
      }

}
