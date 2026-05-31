package com.decade.practice.chatsettings.application.ports.out;

import com.decade.practice.chatsettings.domain.messages.PreferenceMessage;

public interface PreferenceNotifier {
      void notify(String chatId, PreferenceMessage message);
}
