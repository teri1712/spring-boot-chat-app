package com.decade.practice.chatorchestrator.application.ports.in;

import java.util.Set;
import java.util.UUID;

public record CreateGroupChatCommand(
          UUID callerId,
          Set<UUID> partnerIds,
          String roomName
) {
}
