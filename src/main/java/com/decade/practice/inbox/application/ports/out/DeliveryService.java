package com.decade.practice.inbox.application.ports.out;

import com.decade.practice.inbox.domain.messages.InboxLogMessage;

public interface DeliveryService {

      void send(InboxLogMessage message);

}
