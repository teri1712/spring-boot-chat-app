package com.decade.practice.inbox.application.ports.in;

import com.decade.practice.inbox.dto.PostingResponse;

public interface ParticipantPlacement<C extends ParticipantCommand> {
      PostingResponse place(C participantCommand);
}
