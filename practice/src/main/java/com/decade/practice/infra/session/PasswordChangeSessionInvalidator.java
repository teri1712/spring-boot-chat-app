package com.decade.practice.infra.session;

import com.decade.practice.application.events.AccountEventListener;
import com.decade.practice.application.usecases.TokenService;
import com.decade.practice.persistence.jpa.entities.User;
import com.decade.practice.utils.TokenUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;

@Component
@AllArgsConstructor
public class PasswordChangeSessionInvalidator implements AccountEventListener {

    private static final String INVALIDATED_TOKENS = "INVALIDATED_TOKENS";

    private final TokenService credentialService;

    private static HttpServletRequest getCurrentRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    }

    private static Authentication getCurrentAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    @Override
    public void beforePasswordChanged(User account) {
        invalidateTokens();
    }

    @Override
    public void afterPasswordChanged(User account, boolean success) {
        try {
            if (success) {
                invalidateSessions();
                reactivateCurrentToken();
            } else {
                restoreTokens();
            }
        } finally {
            // Unbinds the invalidated tokens from the transaction if they're still present
            if (TransactionSynchronizationManager.hasResource(INVALIDATED_TOKENS)) {
                TransactionSynchronizationManager.unbindResource(INVALIDATED_TOKENS);
            }
        }
    }

    private void invalidateSessions() {
        HttpSession currentSession = getCurrentRequest().getSession(false);
        Object principal = getCurrentAuthentication().getPrincipal();
    }

    @SuppressWarnings("unchecked")
    private void restoreTokens() {
        Object bound = TransactionSynchronizationManager.getResource(INVALIDATED_TOKENS);
        if (bound == null) {
            return;
        }

        List<String> deletedTokens = (List<String>) bound;
        String username = getCurrentAuthentication().getName();

        // Add back any tokens that were previously removed
        for (String token : deletedTokens) {
            credentialService.add(username, token);
        }
    }

    private void reactivateCurrentToken() {
        String username = getCurrentAuthentication().getName();
        String refreshToken = TokenUtils.extractRefreshToken(getCurrentRequest());

        if (refreshToken == null) {
            return;
        }
        // Re-add the current request's refresh token
        credentialService.add(username, refreshToken);
    }

    private void invalidateTokens() {
        String username = getCurrentAuthentication().getName();
        List<String> deletedTokens = credentialService.evict(username);

        // Prepare for restoration in case the password change fails
        TransactionSynchronizationManager.bindResource(INVALIDATED_TOKENS, deletedTokens);
    }
}