package com.decade.practice.engagement.application.ports.in;

import com.decade.practice.engagement.dto.ChatEventResponse;

public interface ParticipantPlacement<C extends ParticipantCommand> {
      ChatEventResponse place(C participantCommand);
}
