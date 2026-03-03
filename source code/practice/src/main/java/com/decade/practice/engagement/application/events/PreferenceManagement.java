package com.decade.practice.engagement.application.events;

import com.decade.practice.engagement.application.ports.out.ChatRepository;
import com.decade.practice.engagement.application.ports.out.PreferenceNotifier;
import com.decade.practice.engagement.domain.Chat;
import com.decade.practice.engagement.domain.ParticipantId;
import com.decade.practice.engagement.domain.events.PreferenceChatEventAccepted;
import lombok.AllArgsConstructor;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Service;


@Service
@AllArgsConstructor
public class PreferenceManagement {

      private final ChatRepository chats;
      private final PreferenceNotifier notifier;

      @ApplicationModuleListener
      public void on(PreferenceChatEventAccepted preferencePlaced) {

            ParticipantId participantId = new ParticipantId(preferencePlaced.getSenderId(), preferencePlaced.getSnapshot().chatId());
            String chatId = participantId.chatId();
            Chat chat = chats.findById(chatId).orElseThrow();

            if (preferencePlaced.getIconId() != null)
                  chat.updateIcon(preferencePlaced.getIconId());
            if (preferencePlaced.getTheme() != null) {
                  chat.updateTheme(preferencePlaced.getTheme());
            }
            if (preferencePlaced.getRoomName() != null)
                  chat.updateRoomName(preferencePlaced.getRoomName());
            if (preferencePlaced.getRoomAvatar() != null)
                  chat.updateAvatar(preferencePlaced.getRoomAvatar());

            notifier.notify(chatId, chat.getPreference());
            chats.save(chat);
      }

}
