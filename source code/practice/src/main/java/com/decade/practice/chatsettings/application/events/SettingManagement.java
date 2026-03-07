package com.decade.practice.chatsettings.application.events;

import com.decade.practice.chatsettings.domain.Setting;
import com.decade.practice.chatsettings.ports.out.SettingRepository;
import com.decade.practice.inbox.domain.events.ChatEventCreated;
import lombok.AllArgsConstructor;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class SettingManagement {

      private final SettingRepository settings;

      @ApplicationModuleListener
      public void on(ChatEventCreated event) {
            settings.findByIdentifier(event.getChatId())
                      .ifPresent(Setting::refreshLastActivity);
      }
}
