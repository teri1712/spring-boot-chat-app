package com.decade.practice.application.services;

import com.decade.practice.application.usecases.SessionService;
import com.decade.practice.application.usecases.TempResource;
import com.decade.practice.application.usecases.TokenStore;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class TokenSessionService implements SessionService {

    private final TokenStore tokenStore;
    private final TempResource tempResource;

    @Override
    public void invalidate(String username, String retainedToken) {
        if (retainedToken != null && !tokenStore.has(username, retainedToken))
            throw new IllegalArgumentException("Invalid token");
        List<String> tokens = tokenStore.evict(username);
        if (retainedToken != null)
            tokenStore.add(username, retainedToken);
        tempResource.put(tokens);
    }

    @Override
    public void restoreToPreviousSessions(String username) {
        List<String> previousTokens = (List<String>) tempResource.get();
        if (previousTokens != null) {
            tokenStore.evict(username);
            for (String session : previousTokens) {
                tokenStore.add(username, session);
            }
        }
    }

}
