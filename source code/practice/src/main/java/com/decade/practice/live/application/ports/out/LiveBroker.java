package com.decade.practice.live.application.ports.out;

import com.decade.practice.live.domain.events.JoinerTyped;

public interface LiveBroker {

      void send(JoinerTyped joinerTyped);

      void subLive(String liveId);

      void unSubLive(String liveId);

}
