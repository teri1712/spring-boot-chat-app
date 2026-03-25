package com.decade.practice.live.application.events;

import com.decade.practice.live.application.ports.out.RoomBroker;
import com.decade.practice.live.domain.events.JoinerJoined;
import com.decade.practice.live.domain.events.JoinerLeaved;
import com.decade.practice.live.domain.events.JoinerTyped;
import com.decade.practice.live.dto.TypeMessage;
import lombok.AllArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class LiveManagement {
      private final RoomBroker broker;

      @EventListener
      public void on(JoinerJoined joined) {
            broker.subRoom(joined.chatId());
      }

      @EventListener
      public void on(JoinerTyped typeEvent) {
            broker.send(new TypeMessage(typeEvent.userId(), typeEvent.avatar(), typeEvent.chatId(), typeEvent.at()));
      }

      @EventListener
      public void on(JoinerLeaved leaved) {
            broker.unSubRoom(leaved.chatId());
      }
}
