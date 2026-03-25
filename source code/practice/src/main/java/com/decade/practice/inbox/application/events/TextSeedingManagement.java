package com.decade.practice.inbox.application.events;

import com.decade.practice.inbox.application.ports.out.TextRepository;
import com.decade.practice.inbox.domain.events.TextAdded;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Profile("dev")
@Service
@RequiredArgsConstructor
public class TextSeedingManagement {

      private final TextRepository texts;
      private final ApplicationEventPublisher publisher;

      @EventListener(ApplicationReadyEvent.class)
      @Transactional
      public void onApplicationReady() {
            texts.findAll().forEach(text ->
                      publisher.publishEvent(new TextAdded(text.getSequenceId(), text.getContent(), text.getChatId(), text.getCreatedAt(), text.getPostingId(), text.getSenderId())));
            log.debug("Seeding complete {}", texts.count());
      }
}
