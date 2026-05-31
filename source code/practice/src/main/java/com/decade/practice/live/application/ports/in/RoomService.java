package com.decade.practice.live.application.ports.in;

import com.decade.practice.live.application.ports.out.LivenessBroker;

import java.util.UUID;

public abstract class RoomService extends LiveChatTopic {
    public RoomService(LivenessBroker broker) {
        super(broker);
    }

    public abstract void type(String chatId, UUID userId, String avatar);
}
