package com.decade.practice.chatsettings.ports.out;

import com.decade.practice.chatsettings.domain.Preference;

public interface PreferenceNotifier {
      void notify(String chatId, Preference preference);
}
