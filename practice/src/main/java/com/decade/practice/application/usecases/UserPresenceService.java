package com.decade.practice.application.usecases;

import com.decade.practice.domain.OnlineStatus;
import com.decade.practice.domain.entities.User;

import java.time.Instant;
import java.util.List;

public interface UserPresenceService {

        OnlineStatus set(User user, long at);

        OnlineStatus set(String username, long at);

        default OnlineStatus set(User user) {
                return set(user.getUsername(), Instant.now().getEpochSecond());
        }

        default OnlineStatus set(String username) {
                return set(username, Instant.now().getEpochSecond());
        }

        OnlineStatus get(String username);

        List<OnlineStatus> getOnlineList(String username);
}