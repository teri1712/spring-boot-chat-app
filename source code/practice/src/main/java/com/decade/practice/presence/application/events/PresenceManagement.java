package com.decade.practice.presence.application.events;

import com.decade.practice.presence.application.ports.out.PresenceRepository;
import com.decade.practice.presence.domain.Presence;
import com.decade.practice.web.events.ConnectionInteracted;
import lombok.RequiredArgsConstructor;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class PresenceManagement {

      private final PresenceRepository presences;

      @ApplicationModuleListener
      public void on(ConnectionInteracted event) {
            Presence presence = new Presence(event.userId(), Instant.now());
            presences.save(presence);
      }
}
