package com.decade.practice.engagement.api;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface EngagementApi {
      boolean canRead(String chatId, UUID userId);

      boolean canWrite(String chatId, UUID userId);

      ChatPolicyInfo createGroup(UUID callerId, Set<UUID> participants);

      DirectInfo createDirect(UUID callerId, UUID partnerId) throws ChatIdentifierUniqueException;

      Optional<ChatPolicyInfo> find(String chatId, UUID userId);
}