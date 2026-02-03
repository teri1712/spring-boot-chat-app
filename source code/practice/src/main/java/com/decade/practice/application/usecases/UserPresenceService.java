package com.decade.practice.application.usecases;

import com.decade.practice.infra.security.jwt.JwtUser;
import com.decade.practice.persistence.redis.OnlineStatus;

import java.time.Instant;
import java.util.List;

public interface UserPresenceService {

    OnlineStatus set(JwtUser principal, Instant at);

    OnlineStatus get(String username);

    // TODO: Paging
    List<OnlineStatus> getOnlineList(String username);
}