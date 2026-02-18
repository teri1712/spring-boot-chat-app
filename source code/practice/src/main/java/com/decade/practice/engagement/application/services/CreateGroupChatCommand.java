package com.decade.practice.engagement.application.services;

import java.util.UUID;

public record CreateGroupChatCommand(
        UUID callerId,
        UUID partnerId,
        String roomName
) {
}
