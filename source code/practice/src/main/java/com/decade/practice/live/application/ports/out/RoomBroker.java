package com.decade.practice.live.application.ports.out;

import com.decade.practice.live.dto.TypeMessage;

public interface RoomBroker {

      void send(TypeMessage message);

      void subRoom(String chatId);

      void unSubRoom(String chatId);

}
