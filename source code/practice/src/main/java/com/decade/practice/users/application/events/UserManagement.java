package com.decade.practice.users.application.events;

import com.decade.practice.users.api.events.IntegrationUserCreated;
import com.decade.practice.users.domain.events.UserCreated;
import com.decade.practice.users.utils.GenderUtils;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
@AllArgsConstructor
public class UserManagement {

      private final ApplicationEventPublisher applicationEventPublisher;

      @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
      public void on(UserCreated userCreated) {
            String gender = GenderUtils.inspect(userCreated.gender());
            applicationEventPublisher.publishEvent(new IntegrationUserCreated(userCreated.userId(), userCreated.username(), userCreated.name(), gender, userCreated.dob(), userCreated.avatar()));
      }
}
