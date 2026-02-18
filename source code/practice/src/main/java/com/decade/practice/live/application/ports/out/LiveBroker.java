package com.decade.practice.live.application.ports.out;

import com.decade.practice.live.domain.LiveChatId;
import com.decade.practice.live.dto.TypeMessage;

public interface LiveBroker {

    void send(TypeMessage typeEvent);

    void subLive(LiveChatId liveChatId);

    void unSubLive(LiveChatId liveChatId);

}
