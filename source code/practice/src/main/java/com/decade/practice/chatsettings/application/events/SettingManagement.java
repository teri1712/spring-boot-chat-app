package com.decade.practice.chatsettings.application.events;

import com.decade.practice.chatsettings.application.ports.out.PreferenceNotifier;
import com.decade.practice.chatsettings.domain.events.PreferenceChanged;
import com.decade.practice.chatsettings.dto.PreferenceMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
@AllArgsConstructor
public class SettingManagement {

      private final PreferenceMapper preferenceMapper;
      private final PreferenceNotifier notifier;


      @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
      public void on(PreferenceChanged event) {
            notifier.notify(event.getChatId(), preferenceMapper.map(event));
      }
}
