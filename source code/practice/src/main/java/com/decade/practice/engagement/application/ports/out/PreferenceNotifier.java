package com.decade.practice.engagement.application.ports.out;

import com.decade.practice.engagement.domain.Preference;

public interface PreferenceNotifier {
      void notify(String chatId, Preference preference);
}
