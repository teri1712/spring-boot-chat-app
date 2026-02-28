package com.decade.practice.users.application.events;

import com.decade.practice.users.api.events.IntegrationUserCreated;
import com.decade.practice.users.application.ports.out.AuthenticationRefreshToken;
import com.decade.practice.users.application.ports.out.TempResource;
import com.decade.practice.users.application.ports.out.TokenStore;
import com.decade.practice.users.domain.TokenRetentionPolicy;
import com.decade.practice.users.domain.events.UserCreated;
import com.decade.practice.users.domain.events.UserPasswordChangedEvent;
import com.decade.practice.users.utils.GenderUtils;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.nio.file.AccessDeniedException;
import java.util.List;

@Service
@AllArgsConstructor
public class UserManagement {
      private final TokenStore tokenStore;
      private final TempResource tempResource;
      private final ApplicationEventPublisher applicationEventPublisher;
      private final AuthenticationRefreshToken authenticationRefreshToken;
      private final TokenRetentionPolicy tokenRetentionPolicy;

      @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
      public void onPasswordChanged(UserPasswordChangedEvent event) throws AccessDeniedException {
            String username = event.username();
            String current = authenticationRefreshToken.get();
            List<String> tokens = tokenStore.evict(username);
            if (current != null) {
                  tokenRetentionPolicy.apply(username, current);
                  tokenStore.add(username, current);
            }
            tempResource.put(tokens);
      }


      @TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
      private void onPasswordChangeFailed(UserPasswordChangedEvent event) {
            String username = event.username();
            List<String> previousTokens = (List<String>) tempResource.get();
            if (previousTokens != null) {
                  tokenStore.add(username, previousTokens.toArray(new String[0]));
            }
      }

      @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
      public void on(UserCreated userCreated) {
            String gender = GenderUtils.inspect(userCreated.gender());
            applicationEventPublisher.publishEvent(new IntegrationUserCreated(userCreated.userId(), userCreated.username(), userCreated.name(), gender, userCreated.dob(), userCreated.avatar()));
      }
}
