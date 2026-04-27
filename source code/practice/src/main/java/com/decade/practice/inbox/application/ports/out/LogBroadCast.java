package com.decade.practice.inbox.application.ports.out;

import com.decade.practice.inbox.domain.messages.InboxLogMessage;

public interface LogSaver {
    void save(InboxLogMessage message);
}
