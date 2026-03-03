package com.decade.practice.inbox.application.events;

import com.decade.practice.engagement.domain.events.PreferenceChatEventAccepted;
import com.decade.practice.inbox.application.ports.out.MessageRepository;
import com.decade.practice.inbox.domain.Preference;
import lombok.AllArgsConstructor;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class PreferenceListener {

      private final MessageRepository messages;

      @ApplicationModuleListener
      public void on(PreferenceChatEventAccepted event) {
            messages.save(new Preference(event.getChatEventId(), event.getSenderId(), event.getSnapshot().chatId(), event.getCreatedAt(), event.getIconId(), event.getRoomName(), event.getRoomAvatar(), event.getTheme()));
      }

}
