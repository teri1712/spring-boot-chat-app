package com.decade.practice.engagement.application.ports.in;

import com.decade.practice.engagement.dto.ReceiptResponse;

public interface ParticipantPlacement {
    ReceiptResponse place(EventCommand eventCommand);
}
