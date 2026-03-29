package com.decade.practice.inbox.application.events;

import com.decade.practice.inbox.application.ports.out.TextRepository;
import com.decade.practice.inbox.domain.events.TextAdded;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TextSeedingManagement {

      private final TextRepository texts;
      private final ApplicationEventPublisher publisher;

      @EventListener(ApplicationReadyEvent.class)
      @Transactional
      public void onApplicationReady() {
            UUID luffy = UUID.fromString("00000000-0000-0000-0000-000000000001");
            UUID nami = UUID.fromString("00000000-0000-0000-0000-000000000003");
            UUID chopper = UUID.fromString("00000000-0000-0000-0000-000000000004");
            UUID zoro = UUID.fromString("00000000-0000-0000-0000-000000000005");

            texts.findAllBySenderIdIsIn(List.of(luffy, nami, zoro, chopper)).forEach(text ->
                      publisher.publishEvent(new TextAdded(text.getSequenceId(), text.getContent(), text.getChatId(), text.getCreatedAt(), text.getPostingId(), text.getSenderId())));
            log.debug("Seeding complete {}", texts.count());
      }
}
