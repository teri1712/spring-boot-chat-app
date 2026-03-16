package com.decade.practice.inbox.application.events;

import com.decade.practice.inbox.application.ports.out.RoomRepository;
import com.decade.practice.inbox.domain.events.RoomCreated;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Profile("dev")
@Service
@RequiredArgsConstructor
public class RoomSeedingManagement {

      private final RoomRepository rooms;
      private final ApplicationEventPublisher publisher;

      @EventListener(ApplicationReadyEvent.class)
      @Transactional
      public void onApplicationReady() {
            rooms.findAll().forEach(room -> {
                  publisher.publishEvent(new RoomCreated(room.getChatId(), room.getRepresentatives()));
            });

      }
}
