package com.decade.practice.engagement.application.events;

import com.decade.practice.engagement.domain.services.StalkPolicy;
import com.decade.practice.web.events.ConnectionInteracted;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Profile("dev")
@Service
@RequiredArgsConstructor
public class StalkSeedingManagement {

      private final StalkPolicy stalkPolicy;
      private final ApplicationEventPublisher publisher;

      @EventListener(ApplicationReadyEvent.class)
      @Transactional
      public void onApplicationReady() {
            UUID luffy = UUID.fromString("00000000-0000-0000-0000-000000000001");
            UUID nami = UUID.fromString("00000000-0000-0000-0000-000000000003");
            UUID chopper = UUID.fromString("00000000-0000-0000-0000-000000000004");
            UUID zoro = UUID.fromString("00000000-0000-0000-0000-000000000005");

            for (int i = 0; i < 10; i++) {
                  stalkPolicy.apply(luffy, nami);
            }
            for (int i = 0; i < 5; i++) {
                  stalkPolicy.apply(luffy, chopper);
            }
            for (int i = 0; i < 5; i++) {
                  stalkPolicy.apply(luffy, zoro);
            }

            publisher.publishEvent(new ConnectionInteracted(luffy, null, Instant.now(), null));
            publisher.publishEvent(new ConnectionInteracted(nami, null, Instant.now(), null));
            publisher.publishEvent(new ConnectionInteracted(zoro, null, Instant.now(), null));
            publisher.publishEvent(new ConnectionInteracted(chopper, null, Instant.now(), null));

      }
}
