package com.decade.practice.chat.application.ports.in;

import java.util.UUID;

public record CreateGroupChatCommand(
          UUID callerId,
          UUID partnerId,
          String roomName
) {
}
