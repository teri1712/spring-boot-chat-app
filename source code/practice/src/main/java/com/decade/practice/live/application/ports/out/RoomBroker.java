package com.decade.practice.live.application.ports.out;

import com.decade.practice.live.dto.TypeMessage;

import java.util.UUID;

public interface RoomBroker {

    void send(TypeMessage message);

    void subRoom(String chatId, UUID userId);

    void unSubRoom(String chatId, UUID userId);

}
