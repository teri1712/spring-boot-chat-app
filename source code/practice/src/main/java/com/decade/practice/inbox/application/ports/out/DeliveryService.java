package com.decade.practice.inbox.application.ports.out;

import com.decade.practice.inbox.dto.InboxLogResponse;

public interface DeliveryService {

      void send(InboxLogResponse inboxLogResponse);

}
