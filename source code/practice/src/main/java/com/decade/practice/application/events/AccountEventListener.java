package com.decade.practice.application.events;

import com.decade.practice.application.services.outbox.OutboxStore;
import com.decade.practice.application.usecases.SessionService;
import com.decade.practice.dto.events.UserCreatedEvent;
import com.decade.practice.dto.events.UserPasswordChangedEvent;
import com.decade.practice.utils.TokenUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
@AllArgsConstructor
public class AccountEventListener {

    private final SessionService sessionService;
    private final OutboxStore outboxStore;

    private static HttpServletRequest getCurrentRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    }

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void onUserCreated(UserCreatedEvent userCreatedEvent) {
        outboxStore.save(userCreatedEvent.getUsername(), "users", userCreatedEvent);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
    public void onUserCreatedFailed(UserCreatedEvent userCreatedEvent) {
    }

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void onPasswordChanged(UserPasswordChangedEvent event) {
        String refreshToken = TokenUtils.extractRefreshToken(getCurrentRequest());
        sessionService.invalidate(event.username(), refreshToken);
    }


    @TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
    private void onPasswordChangeFailed(UserPasswordChangedEvent event) {
        sessionService.restoreToPreviousSessions(event.username());
    }

}