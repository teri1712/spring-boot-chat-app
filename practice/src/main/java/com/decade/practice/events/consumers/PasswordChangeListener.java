package com.decade.practice.events.consumers;

import com.decade.practice.application.usecases.SessionService;
import com.decade.practice.dto.UserPasswordChangedEvent;
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
public class PasswordChangeListener {

    private final SessionService sessionService;

    private static HttpServletRequest getCurrentRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    }


    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void beforePasswordChanged(UserPasswordChangedEvent event) {
        String refreshToken = TokenUtils.extractRefreshToken(getCurrentRequest());
        sessionService.invalidate(event.username(), refreshToken);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
    private void restoreTokens(UserPasswordChangedEvent event) {
        sessionService.restoreToPreviousSessions(event.username());
    }

}