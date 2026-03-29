package com.decade.practice.inbox.application.events;

import com.decade.practice.inbox.application.ports.out.RoomRepository;
import com.decade.practice.inbox.domain.events.RoomCreated;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RoomSeedingManagement {

      private final RoomRepository rooms;
      private final ApplicationEventPublisher publisher;

      @EventListener(ApplicationReadyEvent.class)
      @Transactional
      public void onApplicationReady() {
            UUID luffy = UUID.fromString("00000000-0000-0000-0000-000000000001");
            UUID nami = UUID.fromString("00000000-0000-0000-0000-000000000003");
            UUID chopper = UUID.fromString("00000000-0000-0000-0000-000000000004");
            UUID zoro = UUID.fromString("00000000-0000-0000-0000-000000000005");

            rooms.findAllByCreatorIsIn(List.of(luffy, nami, chopper, zoro)).forEach(room -> {
                  publisher.publishEvent(new RoomCreated(room.getChatId(), room.getCreator(), room.getLastActivity(), room.getRepresentatives()));
            });
      }
}
