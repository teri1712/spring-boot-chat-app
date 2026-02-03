package com.decade.practice.application.usecases;

public interface SessionService {
    void invalidate(String username, String sessionId);

    void restoreToPreviousSessions(String username);
}
