package com.decade.practice.live.application.ports.in;

import com.decade.practice.live.domain.LiveChatId;

import java.util.UUID;

public interface LiveService {
    void join(LiveChatId liveChatId, UUID userId);

    void send(LiveChatId liveChatId, UUID userId);

    void leave(LiveChatId liveChatId, UUID userId);
}
