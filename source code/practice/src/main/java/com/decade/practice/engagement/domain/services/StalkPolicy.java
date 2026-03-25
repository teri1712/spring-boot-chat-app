package com.decade.practice.engagement.domain.services;

import com.decade.practice.engagement.domain.events.StalkEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StalkPolicy {

      private final ApplicationEventPublisher publisher;

      public void apply(UUID caller, UUID target) {
            publisher.publishEvent(new StalkEvent(caller, target));
      }
}
