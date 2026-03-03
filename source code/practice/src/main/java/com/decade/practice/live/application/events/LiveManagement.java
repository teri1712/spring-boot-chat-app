package com.decade.practice.live.application.events;

import com.decade.practice.live.application.ports.out.LiveBroker;
import com.decade.practice.live.domain.events.JoinerLeaved;
import com.decade.practice.live.domain.events.JoinerTyped;
import com.decade.practice.live.domain.events.LiveJoined;
import lombok.AllArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class LiveManagement {
      private final LiveBroker broker;

      @EventListener
      public void on(LiveJoined joined) {
            broker.subLive(joined.liveChatId());
      }

      @EventListener
      public void on(JoinerTyped typed) {
            broker.send(typed);
      }

      @EventListener
      public void on(JoinerLeaved leaved) {
            broker.unSubLive(leaved.chatId());
      }
}
