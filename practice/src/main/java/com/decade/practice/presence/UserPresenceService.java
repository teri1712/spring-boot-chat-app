package com.decade.practice.presence;

import com.decade.practice.models.OnlineStatus;
import com.decade.practice.models.domain.entity.User;

import java.time.Instant;
import java.util.List;

public interface UserPresenceService {

        OnlineStatus set(User user, long at);

        default OnlineStatus set(User user) {
                return set(user, Instant.now().getEpochSecond());
        }

        OnlineStatus get(String username);

        List<OnlineStatus> getOnlineList(String username);
}