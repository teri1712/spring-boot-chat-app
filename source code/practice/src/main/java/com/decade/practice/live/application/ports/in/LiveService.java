package com.decade.practice.live.application.ports.in;

import java.util.UUID;

public interface LiveService {
      void join(String liveChatId, UUID userId, String avatar);

      void send(String liveChatId, UUID userId, String avatar);

      void leave(String liveChatId, UUID userId, String avatar);
}
