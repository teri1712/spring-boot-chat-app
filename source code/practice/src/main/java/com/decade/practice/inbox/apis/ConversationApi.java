package com.decade.practice.inbox.apis;

import java.util.Set;
import java.util.UUID;

public interface ConversationApi {
      void create(String chatId, UUID caller, Set<UUID> participants, String name);
}
