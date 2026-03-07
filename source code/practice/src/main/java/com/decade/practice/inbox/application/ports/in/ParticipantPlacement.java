package com.decade.practice.inbox.application.ports.in;

import com.decade.practice.inbox.dto.ChatEventResponse;

public interface ParticipantPlacement<C extends ParticipantCommand> {
      ChatEventResponse place(C participantCommand);
}
