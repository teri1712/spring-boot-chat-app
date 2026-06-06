package com.decade.practice.web;

import com.decade.practice.web.events.ConnectionInteracted;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Transactional
@Component
@AllArgsConstructor
public class ConnectionInteractionService {

      private final ApplicationEventPublisher publisher;

      public void publish(UUID userId, String ip, String agent) {
            ConnectionInteracted event = new ConnectionInteracted(userId, ip, Instant.now(), agent);
            publisher.publishEvent(event);
      }
}
