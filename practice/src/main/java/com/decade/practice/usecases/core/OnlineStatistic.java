package com.decade.practice.usecases.core;

import com.decade.practice.entities.OnlineStatus;
import com.decade.practice.entities.domain.entity.User;

import java.time.Instant;
import java.util.List;

public interface OnlineStatistic {

      OnlineStatus set(User user, long at);

      default OnlineStatus set(User user) {
            return set(user, Instant.now().getEpochSecond());
      }

      OnlineStatus get(String username);

      List<OnlineStatus> getOnlineList(String username);
}