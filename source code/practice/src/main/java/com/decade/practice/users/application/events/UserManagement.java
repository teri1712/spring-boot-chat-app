package com.decade.practice.users.application.events;

import com.decade.practice.users.api.events.IntegrationUserCreated;
import com.decade.practice.users.application.ports.out.TempResource;
import com.decade.practice.users.application.ports.out.TokenStore;
import com.decade.practice.users.domain.User;
import com.decade.practice.users.domain.events.UserCreated;
import com.decade.practice.users.domain.events.UserPasswordChangedEvent;
import com.decade.practice.web.security.TokenUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;

@Service
@AllArgsConstructor
public class UserManagement {
    private final TokenStore tokenStore;
    private final TempResource tempResource;
    private final ApplicationEventPublisher applicationEventPublisher;

    private static HttpServletRequest getCurrentRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    }

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void onPasswordChanged(UserPasswordChangedEvent event) {
        String username = event.username();
        String retained = TokenUtils.extractRefreshToken(getCurrentRequest());
        if (retained != null && !tokenStore.has(username, retained))
            throw new IllegalArgumentException("Invalid token");
        List<String> tokens = tokenStore.evict(username);
        if (retained != null)
            tokenStore.add(username, retained);
        tempResource.put(tokens);
    }


    @TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
    private void onPasswordChangeFailed(UserPasswordChangedEvent event) {
        String username = event.username();
        List<String> previousTokens = (List<String>) tempResource.get();
        if (previousTokens != null) {
            tokenStore.evict(username);
            for (String session : previousTokens) {
                tokenStore.add(username, session);
            }
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void on(UserCreated userCreated) {
        String gender;
        if (userCreated.gender() == User.MALE) {
            gender = "Male";
        } else if (userCreated.gender() == User.FEMALE) {
            gender = "Female";
        }
        gender = "Unknown";
        applicationEventPublisher.publishEvent(new IntegrationUserCreated(userCreated.userId(), userCreated.username(), userCreated.name(), gender, userCreated.dob(), userCreated.avatar()));
    }
}
